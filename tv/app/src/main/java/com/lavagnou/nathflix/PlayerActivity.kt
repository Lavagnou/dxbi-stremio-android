package com.lavagnou.nathflix

import android.Manifest
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Rational
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import androidx.media3.ui.SubtitleView
import androidx.lifecycle.Lifecycle
import com.google.common.util.concurrent.ListenableFuture
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * The native player: plays the stream Stremio picked (MKV, HEVC, HDR and surround sound included,
 * as far as the device's hardware can), with the addons' subtitles. Reports its position back so the
 * watch progress is kept. A TV gets Media3's controls, steered with the remote; a phone or tablet
 * gets Netflix-like touch controls ([MobilePlayerControls]), picture-in-picture, Next episode and
 * playback that carries on in the background ([PlaybackService]).
 */
@OptIn(UnstableApi::class)
class PlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var titleBar: View
    private lateinit var seekFlash: TextView
    private var player: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var closed = false
    private var lastError: String? = null
    private var lastText: List<String?> = emptyList()

    // Phones and tablets
    private val mobile by lazy { Device.isMobile(this) }
    private var controls: MobilePlayerControls? = null
    private var session: MediaSession? = null
    private var controller: ListenableFuture<MediaController>? = null
    private var pipWanted = false
    private var background = false
    private var autoNext = false
    // Where the page goes for the next episode (its player, or its list of streams), and whether
    // Next episode has taken the skip button's place
    private var nextRoute: String? = null
    private var nextShowing = false

    private val reportProgress = object : Runnable {
        override fun run() {
            report()
            handler.postDelayed(this, PROGRESS_INTERVAL_MS)
        }
    }
    private val hideFlash = Runnable { seekFlash.visibility = View.GONE }

    // Skip intro / recap / credits: the episode's times (seconds; end -1 = to the end), whether Nathflix
    // skips by itself ("intro": intros and recaps, "all": credits too), and which were skipped already
    private class Segment(val type: String, val start: Double, val end: Double)
    private var segments: List<Segment> = emptyList()
    private var autoSkip = "off"
    private val autoSkipped = mutableSetOf<Segment>()
    private var showing: Segment? = null
    private lateinit var skipButton: TextView
    // A torrent played on the TV itself: how it's going, while the picture waits for it
    private var torrent = false
    // which torrent (and file) this player reads: a newer film's torrent is never closed by this one
    private var torrentKey: String? = null
    // libtorrent's status call can block: asked off the main thread
    private val statusThread by lazy { Executors.newSingleThreadExecutor() }
    private val torrentStatus = object : Runnable {
        override fun run() {
            val exo = player
            val waiting = exo == null || exo.playbackState == Player.STATE_BUFFERING || exo.playbackState == Player.STATE_IDLE
            val ctx = this@PlayerActivity
            if (!torrent || !waiting) {
                if (TorrentEngine.isStatus(ctx, seekFlash.text)) seekFlash.visibility = View.GONE
                handler.postDelayed(this, 700)
                return
            }
            statusThread.execute {
                val text = TorrentEngine.status(ctx)
                handler.post {
                    if (closed) return@post
                    if (text != null) {
                        seekFlash.text = text
                        seekFlash.visibility = View.VISIBLE
                    } else if (TorrentEngine.isStatus(ctx, seekFlash.text)) {
                        seekFlash.visibility = View.GONE
                    }
                    handler.postDelayed(this, 700)
                }
            }
        }
    }
    // What the page handed to this player: a newer film's player takes over the reports to the page
    private var ownPlayback: String? = null
    private val checkSkip = object : Runnable {
        override fun run() {
            updateSkip()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        fullScreen()
        setContentView(R.layout.activity_player)
        // A phone's notch or punch hole: the picture and the controls stay clear of it
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player_root)) { view, insets ->
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            view.setPadding(cutout.left, cutout.top, cutout.right, cutout.bottom)
            WindowInsetsCompat.CONSUMED
        }
        playerView = findViewById(R.id.player_view)
        titleBar = findViewById(R.id.title_bar)
        seekFlash = findViewById(R.id.seek_flash)
        skipButton = findViewById(R.id.skip_button)
        skipButton.setOnClickListener { if (nextShowing) playNext() else doSkip() }

        // Opened from the notification after its player had gone: there's nothing to play
        val extra = intent.getStringExtra(EXTRA_STREAM)
        if (extra == null) {
            closed = true
            finish()
            return
        }
        ownPlayback = PlaybackBus.activePlayback
        val stream = JSONObject(extra)
        torrent = stream.optBoolean("torrent", false)
        if (torrent) torrentKey = Uri.parse(stream.optString("url")).path?.removePrefix("/t/")

        // A film still playing in picture-in-picture gives way to the new one
        current?.get()?.takeIf { it !== this }?.closeQuietly(keepTorrent = torrentKey)
        current = WeakReference(this)
        val options = stream.optJSONObject("mobile")
        pipWanted = mobile && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && (options?.optBoolean("pip", true) ?: true) &&
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        background = mobile && (options?.optBoolean("background", true) ?: true)
        autoNext = options?.optBoolean("autoNext", true) ?: true
        nextRoute = stream.optJSONObject("next")?.optString("route")?.ifEmpty { null }
        segments = stream.optJSONArray("skips")?.let { list ->
            (0 until list.length()).mapNotNull { i ->
                val o = list.optJSONObject(i) ?: return@mapNotNull null
                val type = o.optString("type")
                if (type !in setOf("intro", "recap", "credits")) return@mapNotNull null
                Segment(type, o.optDouble("start", 0.0), if (o.isNull("end")) -1.0 else o.optDouble("end", -1.0))
            }
        } ?: emptyList()
        autoSkip = stream.optString("autoSkip", "off")
        findViewById<TextView>(R.id.title).text = stream.optString("title")
        findViewById<TextView>(R.id.subtitle).apply {
            text = stream.optString("subtitle")
            visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        stream.optJSONObject("subtitleStyle")?.let { styleSubtitles(it) }

        val url = stream.optString("url")
        if (url.isEmpty()) {
            finishWith(error = Lang.text(this, R.string.error_no_address))
            return
        }

        // Addons can ask for headers on their links (Stremio's proxyHeaders)
        val headers = mutableMapOf<String, String>()
        stream.optJSONObject("headers")?.let { h -> h.keys().forEach { k -> headers[k] = h.optString(k) } }
        val http = Http.client.newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            // a torrent's first bytes wait for its details and first pieces to come from peers
            .readTimeout(if (torrent) 150L else 30L, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
        // (an addon's own User-Agent replaces ours: Media3 would send both)
        val ownAgent = headers.keys.any { it.equals("User-Agent", ignoreCase = true) }
        val dataSource = OkHttpDataSource.Factory(http)
            .setUserAgent(if (ownAgent) null else "nathflix-tv/${BuildConfigVersion.name(this)}")
            .setDefaultRequestProperties(headers)

        // The device's own decoders first, falling back to software ones when a format isn't supported
        // (audio: FFmpeg, for Dolby, DTS and TrueHD tracks the hardware can't decode)
        val renderers = DefaultRenderersFactory(this)
            .setEnableDecoderFallback(true)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)

        val exo = ExoPlayer.Builder(this, renderers)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSource))
            .setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(), true)
            .setHandleAudioBecomingNoisy(true)
            .setSeekBackIncrementMs(SEEK_STEP_MS)
            .setSeekForwardIncrementMs(SEEK_STEP_MS)
            // playing on with the screen off: Wi-Fi stays awake for the stream
            .apply { if (background) setWakeMode(C.WAKE_MODE_NETWORK) }
            .build()
        player = exo
        playerView.player = exo
        if (mobile) {
            setUpMobile(stream, exo)
        } else {
            // Animated, the controls never fully go: they shrink to a thin bar, and the title (which
            // follows them) stayed on screen over the film
            playerView.setControllerAnimationEnabled(false)
            playerView.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
                titleBar.visibility = visibility
                // The remote starts on play/pause, and the arrows move between the controls from there
                if (visibility == View.VISIBLE) playerView.findViewById<View>(androidx.media3.ui.R.id.exo_play_pause)?.requestFocus()
            })
        }

        // Subtitles start off, like Netflix (and Nathflix's default): pick one from the subtitles button.
        // Files with several audio tracks (dual-audio releases) start in Stremio's audio language
        // (Settings → Player), when they have it
        val audioLanguage = stream.optString("audioLanguage").ifEmpty { null }
        exo.trackSelectionParameters = exo.trackSelectionParameters.buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
            .setPreferredTextLanguage(null)
            .setSelectUndeterminedTextLanguage(false)
            .apply { if (audioLanguage != null) setPreferredAudioLanguage(audioLanguage) }
            .build()

        val subtitles = mutableListOf<MediaItem.SubtitleConfiguration>()
        stream.optJSONArray("subtitles")?.let { list ->
            for (i in 0 until list.length()) {
                val s = list.optJSONObject(i) ?: continue
                val subUrl = s.optString("url").takeIf { it.isNotEmpty() } ?: continue
                subtitles += MediaItem.SubtitleConfiguration.Builder(Uri.parse(subUrl))
                    .setMimeType(subtitleMime(subUrl))
                    .setLanguage(s.optString("lang").ifEmpty { null })
                    .setLabel(s.optString("label").ifEmpty { s.optString("lang") })
                    .setSelectionFlags(0)
                    .build()
            }
        }

        // (what the notification and the lock screen show while it plays in the background)
        val about = MediaMetadata.Builder()
            .setTitle(stream.optString("title"))
            .setArtist(stream.optString("subtitle").ifEmpty { null })
            .apply { stream.optString("background").takeIf { it.startsWith("http") }?.let { setArtworkUri(Uri.parse(it)) } }
            .build()
        val item = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setSubtitleConfigurations(subtitles)
            .setMediaMetadata(about)
            .build()
        exo.setMediaItem(item, stream.optLong("startMs", 0L).coerceAtLeast(0L))
        exo.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state != Player.STATE_ENDED) return
                // Phones and tablets go on to the next episode, like Netflix (unless Up next is off)
                val next = nextRoute
                if (mobile && autoNext && next != null) finishWith(ended = true, playRoute = next) else finishWith(ended = true)
            }

            // A subtitle switched on mid-line only shows from its next line: a seek to where we are
            // brings the current one up straight away
            override fun onTracksChanged(tracks: Tracks) {
                val text = tracks.groups.filter { it.type == C.TRACK_TYPE_TEXT }.flatMap { g -> (0 until g.length).filter { g.isTrackSelected(it) }.map { i -> g.getTrackFormat(i).id } }
                if (text.isNotEmpty() && text != lastText) exo.seekTo(exo.currentPosition)
                lastText = text
            }

            override fun onPlayerError(error: PlaybackException) {
                lastError = (if (torrent) TorrentEngine.failed(this@PlayerActivity) else null) ?: describe(error)
                finishWith(error = lastError)
            }
        })
        exo.prepare()
        exo.playWhenReady = true
        handler.postDelayed(reportProgress, PROGRESS_INTERVAL_MS)
        if (segments.isNotEmpty() || (mobile && nextRoute != null)) handler.postDelayed(checkSkip, 500)
        if (torrent) handler.post(torrentStatus)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            // Back leaves the player straight away, like Netflix (the position is kept); on a phone it
            // first closes an open panel (Speed, Audio & Subtitles, Episodes)
            override fun handleOnBackPressed() {
                if (controls?.back() == true) return
                finishWith()
            }
        })
    }

    /**
     * With the controls hidden, the remote works like Netflix's: left/right skip 10 seconds, OK
     * pauses or plays. With the controls showing, the arrows move between them as usual.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val exo = player ?: return super.dispatchKeyEvent(event)
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_SPACE -> { exo.playWhenReady = !exo.playWhenReady; playerView.showController(); return true }
                KeyEvent.KEYCODE_MEDIA_PLAY -> { exo.play(); return true }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> { exo.pause(); playerView.showController(); return true }
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> { seek(+1); return true }
                KeyEvent.KEYCODE_MEDIA_REWIND -> { seek(-1); return true }
            }
            // OK presses Skip while it's up (and the controls aren't)
            if (showing != null && !playerView.isControllerFullyVisible && (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                doSkip()
                return true
            }
            if (!playerView.isControllerFullyVisible) {
                when (event.keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> { seek(-1); return true }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> { seek(+1); return true }
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        exo.playWhenReady = !exo.playWhenReady
                        playerView.showController()
                        return true
                    }
                    KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> { playerView.showController(); return true }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun segmentEnd(s: Segment, durationMs: Long): Long =
        if (s.end < 0) durationMs else (s.end * 1000).toLong()

    private fun updateSkip() {
        val exo = player ?: return
        val duration = exo.duration.takeIf { it != C.TIME_UNSET } ?: return
        val now = exo.currentPosition
        // (not the last second of a part: pressing then would skip nothing)
        val seg = segments.firstOrNull { now >= (it.start * 1000).toLong() && now < segmentEnd(it, duration) - 1000 }
        val auto = seg != null && (autoSkip == "all" || (autoSkip == "intro" && seg.type != "credits"))
        if (seg != null && auto && seg !in autoSkipped) {
            autoSkipped += seg
            doSkip(seg)
            return
        }
        // Phones and tablets: during the credits, or the last half minute, Next episode takes the
        // skip button's place, like Netflix
        val wantNext = mobile && nextRoute != null && (seg?.type == "credits" || (seg == null && duration - now < NEXT_WINDOW_MS))
        if (wantNext) {
            if (!nextShowing) {
                nextShowing = true
                showing = null
                skipButton.text = Lang.text(this, R.string.player_next_short)
                skipButton.visibility = View.VISIBLE
            }
            return
        }
        if (nextShowing) {
            nextShowing = false
            showing = null
            skipButton.visibility = View.GONE
        }
        if (seg == showing) return
        showing = seg
        if (seg == null) {
            skipButton.visibility = View.GONE
            return
        }
        skipButton.text = Lang.text(this, when (seg.type) {
            "intro" -> R.string.player_skip_intro
            "recap" -> R.string.player_skip_recap
            else -> R.string.player_skip_credits
        })
        skipButton.visibility = View.VISIBLE
        if (!playerView.isControllerFullyVisible) skipButton.requestFocus()
    }

    private fun doSkip(seg: Segment? = showing) {
        val exo = player ?: return
        val s = seg ?: return
        val duration = exo.duration.takeIf { it != C.TIME_UNSET } ?: return
        exo.seekTo(segmentEnd(s, duration).coerceAtMost(duration))
        showing = null
        skipButton.visibility = View.GONE
        playerView.requestFocus()
    }

    private fun seek(direction: Int) {
        val exo = player ?: return
        if (direction > 0) exo.seekForward() else exo.seekBack()
        seekFlash.text = if (direction > 0) "+10 s   ${clock(exo.currentPosition)}" else "−10 s   ${clock(exo.currentPosition)}"
        seekFlash.visibility = View.VISIBLE
        handler.removeCallbacks(hideFlash)
        handler.postDelayed(hideFlash, 900)
    }

    private fun report() {
        if (closed || !ownsPlayback()) return
        val exo = player ?: return
        val duration = exo.duration.takeIf { it != C.TIME_UNSET } ?: 0L
        PlaybackBus.listener?.onProgress(exo.currentPosition, duration, !exo.isPlaying)
    }

    /** Closes the player, keeping the progress; [playRoute]: where the page goes next (another episode) */
    private fun finishWith(ended: Boolean = false, error: String? = null, playRoute: String? = null) {
        if (closed) return
        closed = true
        handler.removeCallbacksAndMessages(null)
        val exo = player
        val duration = exo?.duration?.takeIf { it != C.TIME_UNSET } ?: 0L
        val position = if (ended) duration else exo?.currentPosition ?: 0L
        if (ownsPlayback()) PlaybackBus.closed(position, duration, ended, error, playRoute)
        if (torrent) TorrentEngine.close(torrentKey)
        // the session goes now, not when Android gets round to destroying this screen: the next
        // episode's player may already be starting
        controls?.release()
        releaseSession()
        finish()
    }

    // (false once another film has started: its player reports to the page, not this one)
    private fun ownsPlayback() = PlaybackBus.activePlayback === ownPlayback

    /** Another film started while this one played in picture-in-picture: it just goes */
    private fun closeQuietly(keepTorrent: String?) {
        if (closed) return
        closed = true
        handler.removeCallbacksAndMessages(null)
        // released now, not when Android gets round to destroying it: the new player's session takes
        // the same name, and only one at a time may have it
        controls?.release()
        releaseSession()
        player?.release()
        player = null
        // its torrent stops downloading (unless the new film plays from the same one)
        if (torrent && torrentKey != keepTorrent) TorrentEngine.close(torrentKey)
        finish()
    }

    // ---- Phones and tablets

    private fun setUpMobile(stream: JSONObject, exo: ExoPlayer) {
        playerView.useController = false
        titleBar.visibility = View.GONE
        val episodes = stream.optJSONArray("episodes")?.let { list ->
            (0 until list.length()).mapNotNull { i ->
                val o = list.optJSONObject(i) ?: return@mapNotNull null
                MobilePlayerControls.Episode(o.optString("route"), o.optInt("episode"), o.optString("title"), o.optString("overview"), o.optString("thumbnail"), o.optBoolean("current"))
            }
        } ?: emptyList()
        val c = MobilePlayerControls(this, exo, stream.optString("title"), stream.optString("subtitle"), episodes, stream.optInt("season", 0),
            stream.optJSONObject("next")?.optString("label")?.takeIf { nextRoute != null }, pipWanted)
        // over the picture, under the skip button and the torrent's status
        findViewById<FrameLayout>(R.id.player_root).addView(c.view, 1, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        controls = c

        // Play/pause from headphones, the lock screen, the notification and the picture-in-picture window
        val open = PendingIntent.getActivity(this, 0,
            Intent(this, PlayerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        // (a name of its own: the previous episode's session may not be gone yet)
        val s = MediaSession.Builder(this, exo).setId("nathflix-player-${System.nanoTime()}").setSessionActivity(open).build()
        session = s
        if (background) {
            PlaybackService.session = s
            controller = MediaController.Builder(this, SessionToken(this, ComponentName(this, PlaybackService::class.java))).buildAsync()
            askForNotifications()
        }
        exo.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) = updatePip()
            override fun onVideoSizeChanged(videoSize: VideoSize) = updatePip()
        })
        updatePip()
    }

    fun leave() = finishWith()

    fun playNext() {
        nextRoute?.let { finishWith(playRoute = it) }
    }

    fun playEpisode(route: String) {
        if (route.isNotEmpty()) finishWith(playRoute = route)
    }

    fun enterPip() {
        if (!pipWanted || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        try {
            enterPictureInPictureMode(pipParams())
        } catch (_: Exception) { /* not allowed right now */ }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pipParams(): PictureInPictureParams {
        val size = player?.videoSize
        val shape = if (size != null && size.width > 0 && size.height > 0) size.width.toFloat() / size.height else 16f / 9
        // Android takes shapes between 1:2.39 and 2.39:1 only
        val ratio = Rational((shape.coerceIn(0.42f, 2.39f) * 1000).toInt(), 1000)
        return PictureInPictureParams.Builder().setAspectRatio(ratio).apply {
            // Android 12+: Home turns a playing film into the small window by itself
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setAutoEnterEnabled(player?.isPlaying == true)
                setSeamlessResizeEnabled(false)
            }
        }.build()
    }

    private fun updatePip() {
        if (!pipWanted || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        try {
            setPictureInPictureParams(pipParams())
        } catch (_: Exception) { /* not allowed right now */ }
    }

    // Home (before Android 12, which does it by itself): a playing film goes on in a small window
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (pipWanted && Build.VERSION.SDK_INT < Build.VERSION_CODES.S && player?.isPlaying == true) enterPip()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        controls?.setPip(isInPictureInPictureMode)
        if (isInPictureInPictureMode) {
            skipButton.visibility = View.GONE
            showing = null
            nextShowing = false
        } else if (lifecycle.currentState == Lifecycle.State.CREATED) {
            // the small window was closed (its ✕, or swiped away): the player closes, keeping the progress
            finishWith()
        }
    }

    // Opened again from the notification: it's the film already playing
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    private fun askForNotifications() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        if (prefs.getBoolean("askedNotifications", false)) return
        prefs.edit().putBoolean("askedNotifications", true).apply()
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
    }

    private fun releaseSession() {
        controller?.let { MediaController.releaseFuture(it) }
        controller = null
        if (PlaybackService.session === session) PlaybackService.session = null
        session?.release()
        session = null
        // (unless a new film has handed its session over already)
        if (background && PlaybackService.session == null) stopService(Intent(this, PlaybackService::class.java))
    }

    // Back from another app (or the screen turning on): the bars that came up go away again
    override fun onResume() {
        super.onResume()
        fullScreen()
    }

    private fun fullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onPause() {
        super.onPause()
        // On a TV, leaving the app (Home button) pauses, and the progress so far is kept (a phone
        // decides in onStop: picture-in-picture and playing in the background carry on)
        if (!mobile) player?.pause()
        report()
    }

    override fun onStop() {
        super.onStop()
        // Phones and tablets: with the screen off or Nathflix in the background, the film only goes on
        // when that's switched on in Nathflix settings (its controls are then in the notification)
        if (mobile && !closed && !isInPictureInPictureMode && !background) player?.pause()
    }

    override fun onDestroy() {
        if (!closed) finishWith()
        handler.removeCallbacksAndMessages(null)
        controls?.release()
        controls = null
        releaseSession()
        player?.release()
        player = null
        if (torrent) statusThread.shutdown()
        if (current?.get() === this) current = null
        super.onDestroy()
    }

    /**
     * Stremio's subtitle settings (size, colours, outline, position) instead of the player's white
     * text on black boxes. Colours come as #RRGGBB or #RRGGBBAA.
     */
    private fun styleSubtitles(style: JSONObject) {
        val view = playerView.subtitleView ?: return
        fun color(key: String, fallback: Int): Int {
            val hex = style.optString(key).removePrefix("#")
            val v = hex.toLongOrNull(16) ?: return fallback
            return when (hex.length) {
                6 -> (0xFF000000 or v).toInt()
                8 -> ((v and 0xFF) shl 24 or (v ushr 8)).toInt()
                else -> fallback
            }
        }
        val opacity = (style.optDouble("opacity", 100.0) / 100).coerceIn(0.0, 1.0)
        val text = color("text", Color.WHITE).let { Color.argb((Color.alpha(it) * opacity).toInt(), Color.red(it), Color.green(it), Color.blue(it)) }
        val outline = color("outline", Color.BLACK)
        val typeface = if (style.optBoolean("bold")) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        view.setStyle(CaptionStyleCompat(
            text,
            color("background", Color.TRANSPARENT),
            Color.TRANSPARENT,
            if (Color.alpha(outline) > 0) CaptionStyleCompat.EDGE_TYPE_OUTLINE else CaptionStyleCompat.EDGE_TYPE_NONE,
            outline,
            typeface,
        ))
        // the file's own colours and sizes would win over these otherwise
        view.setApplyEmbeddedStyles(false)
        view.setApplyEmbeddedFontSizes(false)
        val size = style.optDouble("size", 100.0).coerceIn(50.0, 300.0) / 100
        view.setFractionalTextSize((SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * size).toFloat())
        // Stremio's offset is how far up from the bottom of the picture the lines sit
        val offset = style.optDouble("offset", 5.0).coerceIn(0.0, 40.0) / 100
        view.setBottomPaddingFraction(0f)
        view.addOnLayoutChangeListener { v, _, top, _, bottom, _, _, _, _ ->
            val pad = ((bottom - top) * offset).toInt()
            if (v.paddingBottom != pad) v.post { v.setPadding(0, 0, 0, pad) }
        }
    }

    private fun subtitleMime(url: String): String {
        val path = Uri.parse(url).lastPathSegment?.lowercase() ?: ""
        return when {
            path.endsWith(".vtt") -> MimeTypes.TEXT_VTT
            path.endsWith(".ass") || path.endsWith(".ssa") -> MimeTypes.TEXT_SSA
            path.endsWith(".ttml") || path.endsWith(".dfxp") -> MimeTypes.APPLICATION_TTML
            else -> MimeTypes.APPLICATION_SUBRIP
        }
    }

    private fun describe(error: PlaybackException): String = when (error.errorCode) {
        PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> Lang.text(this, R.string.error_refused)
        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> Lang.text(this, R.string.error_connection)
        PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
        PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED,
        PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES ->
            // Audio has a software decoder (FFmpeg) for whatever the hardware can't do; video doesn't
            Lang.text(this, R.string.error_video_format, Device.thisOne(this))
        PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> Lang.text(this, R.string.error_container)
        else -> error.errorCodeName.removePrefix("ERROR_CODE_").lowercase().replace('_', ' ')
    }

    private fun clock(ms: Long): String {
        val s = ms / 1000
        return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60) else "%d:%02d".format(s / 60, s % 60)
    }

    companion object {
        const val EXTRA_STREAM = "stream"
        private const val PROGRESS_INTERVAL_MS = 5000L
        private const val NEXT_WINDOW_MS = 30_000L
        // The player on screen (or in picture-in-picture), so a new film can close it
        private var current: WeakReference<PlayerActivity>? = null
        private const val SEEK_STEP_MS = 10_000L
    }
}
