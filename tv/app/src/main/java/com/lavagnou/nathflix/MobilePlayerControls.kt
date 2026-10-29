package com.lavagnou.nathflix

import android.content.Context
import android.graphics.Typeface
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.TimeBar
import kotlin.math.abs

/**
 * The player's controls on a phone or tablet, made like Netflix's app: a tap shows or hides them,
 * a double tap on the left or right skips 10 seconds back or forward, sliding up or down on the left
 * changes the brightness and on the right the volume. Lock keeps stray touches away while watching.
 * Its texts follow Nathflix's language (Lang). A TV keeps Media3's own controls, steered with the remote.
 */
@OptIn(UnstableApi::class)
class MobilePlayerControls(
    private val activity: PlayerActivity,
    private val player: ExoPlayer,
    title: String,
    subtitle: String,
    /** The season's episodes (the one playing marked), for the Episodes panel; empty for a film */
    private val episodes: List<Episode>,
    private val season: Int,
    private val nextLabel: String?,
    private val canPip: Boolean,
) {
    class Episode(val route: String, val episode: Int, val title: String, val overview: String, val thumbnail: String, val current: Boolean)

    /** The activity in Nathflix's language, for every text and view made here */
    private val ui: Context = Lang.context(activity, R.style.Theme_Nathflix_Player)

    val view: View = LayoutInflater.from(ui).inflate(R.layout.player_mobile_controls, null)
    private val controls: View = view.findViewById(R.id.mc_controls)
    private val play: ImageButton = view.findViewById(R.id.mc_play)
    private val timeBar: DefaultTimeBar = view.findViewById(R.id.mc_timebar)
    private val remaining: TextView = view.findViewById(R.id.mc_remaining)
    private val speedButton: TextView = view.findViewById(R.id.mc_speed)
    private val unlock: View = view.findViewById(R.id.mc_unlock)
    private val tapBack: TextView = view.findViewById(R.id.mc_tap_back)
    private val tapForward: TextView = view.findViewById(R.id.mc_tap_forward)
    private val level: View = view.findViewById(R.id.mc_level)
    private val levelIcon: ImageView = view.findViewById(R.id.mc_level_icon)
    private val levelBar: ProgressBar = view.findViewById(R.id.mc_level_bar)
    private val panel: View = view.findViewById(R.id.mc_panel)
    private val panelBody: FrameLayout = view.findViewById(R.id.mc_panel_body)

    private val main = Handler(Looper.getMainLooper())
    private val audio = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var locked = false
    private var scrubbing = false
    private var inPip = false

    /** Whether the controls are up */
    val shown get() = controls.visibility == View.VISIBLE

    private val hideLater = Runnable { if (player.isPlaying && !scrubbing) hide() }
    private val hideUnlock = Runnable { unlock.visibility = View.GONE }
    private val hideLevel = Runnable { level.visibility = View.GONE }
    private val tick = object : Runnable {
        override fun run() {
            update()
            main.postDelayed(this, 500)
        }
    }

    init {
        // One line at the top, like Netflix: the show, then the episode
        view.findViewById<TextView>(R.id.mc_title).text = if (subtitle.isEmpty()) title else "$title · $subtitle"
        view.findViewById<View>(R.id.mc_back).setOnClickListener { activity.leave() }
        view.findViewById<View>(R.id.mc_pip).apply {
            visibility = if (canPip) View.VISIBLE else View.GONE
            setOnClickListener { activity.enterPip() }
        }
        play.setOnClickListener {
            if (player.isPlaying) player.pause() else player.play()
            update()
            showFor()
        }
        view.findViewById<View>(R.id.mc_rewind).setOnClickListener { skip(-1) }
        view.findViewById<View>(R.id.mc_forward).setOnClickListener { skip(+1) }
        speedButton.setOnClickListener { openSpeed() }
        view.findViewById<View>(R.id.mc_lock).setOnClickListener { lock() }
        view.findViewById<View>(R.id.mc_audio).setOnClickListener { openTracks() }
        view.findViewById<View>(R.id.mc_episodes).apply {
            visibility = if (episodes.size > 1) View.VISIBLE else View.GONE
            setOnClickListener { openEpisodes() }
        }
        view.findViewById<View>(R.id.mc_next).apply {
            visibility = if (nextLabel != null) View.VISIBLE else View.GONE
            setOnClickListener { activity.playNext() }
        }
        // The row's icons are small next to their labels
        for (id in listOf(R.id.mc_speed, R.id.mc_lock, R.id.mc_episodes, R.id.mc_audio, R.id.mc_next)) {
            val button = view.findViewById<TextView>(id)
            val icon = button.compoundDrawablesRelative[0] ?: continue
            icon.setBounds(0, 0, dp(20), dp(20))
            button.setCompoundDrawablesRelative(icon, null, null, null)
        }
        view.findViewById<View>(R.id.mc_panel_close).setOnClickListener { closePanel() }
        unlock.setOnClickListener {
            locked = false
            unlock.visibility = View.GONE
            showFor()
        }

        timeBar.addListener(object : TimeBar.OnScrubListener {
            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                scrubbing = true
                main.removeCallbacks(hideLater)
            }
            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                remaining.text = clock((player.duration - position).coerceAtLeast(0))
            }
            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                scrubbing = false
                if (!canceled) player.seekTo(position)
                showFor()
            }
        })
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                update()
                // paused: the controls stay up, like Netflix
                if (isPlaying) showFor() else if (!locked && !inPip) show()
            }
        })

        val detector = GestureDetector(activity, Gestures())
        view.findViewById<View>(R.id.mc_gestures).setOnTouchListener { _, e ->
            detector.onTouchEvent(e)
            if (e.actionMasked == MotionEvent.ACTION_UP || e.actionMasked == MotionEvent.ACTION_CANCEL) {
                slide = Slide.NONE
                main.postDelayed(hideLevel, 600)
            }
            true
        }
        main.post(tick)
        showFor()
    }

    fun release() = main.removeCallbacksAndMessages(null)

    /** In picture-in-picture only the picture shows */
    fun setPip(on: Boolean) {
        inPip = on
        if (on) {
            closePanel()
            hide()
            unlock.visibility = View.GONE
        } else if (!locked) show()
    }

    // ---- Showing and hiding

    private fun show() {
        if (locked || inPip) return
        controls.visibility = View.VISIBLE
        update()
    }

    /** Up for a few seconds, then gone again while the film plays */
    private fun showFor() {
        show()
        main.removeCallbacks(hideLater)
        main.postDelayed(hideLater, HIDE_AFTER_MS)
    }

    private fun hide() {
        main.removeCallbacks(hideLater)
        controls.visibility = View.GONE
    }

    private fun toggle() = if (shown) hide() else showFor()

    private fun update() {
        play.setImageResource(if (player.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
        val duration = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L
        timeBar.setDuration(duration)
        timeBar.setBufferedPosition(player.bufferedPosition)
        if (!scrubbing) {
            timeBar.setPosition(player.currentPosition)
            // the time left, as Netflix shows it
            remaining.text = if (duration > 0) clock((duration - player.currentPosition).coerceAtLeast(0)) else ""
        }
        val speed = ui.getString(R.string.player_speed, speedText(player.playbackParameters.speed))
        if (speedButton.text != speed) speedButton.text = speed
    }

    private fun lock() {
        locked = true
        hide()
        flashUnlock()
    }

    private fun flashUnlock() {
        unlock.visibility = View.VISIBLE
        main.removeCallbacks(hideUnlock)
        main.postDelayed(hideUnlock, 3000)
    }

    // ---- Skipping ±10 seconds (buttons and double taps)

    private fun skip(direction: Int) {
        if (direction > 0) player.seekForward() else player.seekBack()
        val bubble = if (direction > 0) tapForward else tapBack
        bubble.text = if (direction > 0) "+10 s" else "−10 s"
        bubble.visibility = View.VISIBLE
        bubble.removeCallbacks(bubble.tag as? Runnable)
        val gone = Runnable { bubble.visibility = View.GONE }
        bubble.tag = gone
        bubble.postDelayed(gone, 700)
        update()
        if (shown) showFor()
    }

    // ---- Gestures: tap, double tap, slides for brightness and volume

    private enum class Slide { NONE, BRIGHTNESS, VOLUME, IGNORED }
    private var slide = Slide.NONE
    private var brightness = -1f
    private var volume = 0f

    private inner class Gestures : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent) = true

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (locked) flashUnlock() else toggle()
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (locked) return true
            val w = view.width
            when {
                e.x < w / 3f -> skip(-1)
                e.x > w * 2 / 3f -> skip(+1)
                else -> {
                    if (player.isPlaying) player.pause() else player.play()
                    update()
                }
            }
            return true
        }

        override fun onScroll(down: MotionEvent?, e: MotionEvent, dx: Float, dy: Float): Boolean {
            if (locked || down == null) return false
            val h = view.height.toFloat()
            if (slide == Slide.NONE) {
                // the screen's top and bottom edges belong to Android's own gestures, and a mostly
                // sideways slide isn't for us
                val total = e.y - down.y
                slide = when {
                    down.y < h * 0.1f || down.y > h * 0.9f -> Slide.IGNORED
                    abs(total) < abs(e.x - down.x) -> Slide.IGNORED
                    down.x < view.width / 2f -> Slide.BRIGHTNESS
                    else -> Slide.VOLUME
                }
                if (slide == Slide.BRIGHTNESS) brightness = currentBrightness()
                if (slide == Slide.VOLUME) volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
            }
            // the full height of the screen goes from nothing to everything
            val change = dy / (h * 0.8f)
            when (slide) {
                Slide.BRIGHTNESS -> {
                    brightness = (brightness + change).coerceIn(0.01f, 1f)
                    val attrs = activity.window.attributes
                    attrs.screenBrightness = brightness
                    activity.window.attributes = attrs
                    showLevel(R.drawable.ic_brightness, brightness)
                }
                Slide.VOLUME -> {
                    val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    volume = (volume + change * max).coerceIn(0f, max.toFloat())
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume.toInt(), 0)
                    showLevel(R.drawable.ic_volume, volume / max)
                }
                else -> return false
            }
            return true
        }
    }

    private fun currentBrightness(): Float {
        val set = activity.window.attributes.screenBrightness
        if (set >= 0) return set
        return try {
            Settings.System.getInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
        } catch (_: Exception) {
            0.5f
        }
    }

    private fun showLevel(icon: Int, fraction: Float) {
        main.removeCallbacks(hideLevel)
        levelIcon.setImageResource(icon)
        levelBar.progress = (fraction * 100).toInt()
        level.visibility = View.VISIBLE
    }

    // ---- Panels: Speed, Audio & Subtitles, Episodes (over the picture, like Netflix's)

    private fun openPanel(fill: (FrameLayout) -> Unit) {
        hide()
        panelBody.removeAllViews()
        fill(panelBody)
        panel.visibility = View.VISIBLE
    }

    private fun closePanel() {
        pickedAudio = null
        pickedText = null
        panel.visibility = View.GONE
        if (!inPip) showFor()
    }

    /** Back closes an open panel first */
    fun back(): Boolean {
        if (panel.visibility != View.VISIBLE) return false
        closePanel()
        return true
    }

    private fun text(value: String, size: Float, color: Int, medium: Boolean = false) = TextView(ui).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (medium) typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    private fun heading(value: String) = text(value, 20f, WHITE, medium = true)

    // Speed: five speeds on a line, the one playing lit up
    private fun openSpeed(): Unit = openPanel { body ->
        val now = player.playbackParameters.speed
        val box = LinearLayout(ui).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(40), dp(26), dp(40), dp(24))
        }
        box.addView(heading(ui.getString(R.string.player_speed_title)))
        val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f)
        val line = LinearLayout(ui).apply { orientation = LinearLayout.HORIZONTAL }
        val labels = LinearLayout(ui).apply { orientation = LinearLayout.HORIZONTAL }
        speeds.forEachIndexed { i, speed ->
            val on = abs(now - speed) < 0.01f
            val pick = View.OnClickListener {
                player.setPlaybackSpeed(speed)
                update()
                openSpeed()
            }
            // a stretch of the line on either side of the speed's dot (none past the ends)
            val cell = FrameLayout(ui).apply { setOnClickListener(pick) }
            val halves = LinearLayout(ui).apply { orientation = LinearLayout.HORIZONTAL }
            halves.addView(View(ui).apply { setBackgroundColor(if (i == 0) 0 else GREY_LINE) }, LinearLayout.LayoutParams(0, dp(2), 1f))
            halves.addView(View(ui).apply { setBackgroundColor(if (i == speeds.lastIndex) 0 else GREY_LINE) }, LinearLayout.LayoutParams(0, dp(2), 1f))
            cell.addView(halves, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(2), Gravity.CENTER_VERTICAL))
            val size = if (on) dp(24) else dp(12)
            cell.addView(View(ui).apply { setBackgroundResource(if (on) R.drawable.mc_dot_on else R.drawable.mc_dot) }, FrameLayout.LayoutParams(size, size, Gravity.CENTER))
            line.addView(cell, LinearLayout.LayoutParams(0, dp(48), 1f))
            val name = if (speed == 1f) ui.getString(R.string.player_speed_normal, speedText(speed)) else speedText(speed)
            labels.addView(text(name, 14f, if (on) WHITE else GREY, medium = on).apply {
                gravity = Gravity.CENTER
                setOnClickListener(pick)
            }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        }
        val middle = LinearLayout(ui).apply { orientation = LinearLayout.VERTICAL }
        middle.addView(line)
        middle.addView(labels)
        box.addView(View(ui), LinearLayout.LayoutParams(0, 0, 1f))
        box.addView(middle, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        box.addView(View(ui), LinearLayout.LayoutParams(0, 0, 1.3f))
        body.addView(box, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    // Audio & Subtitles: two lists side by side; nothing changes until Apply, like Netflix
    private class Pick(val group: Tracks.Group?, val index: Int)
    private var pickedAudio: Pick? = null
    private var pickedText: Pick? = null

    private fun openTracks(): Unit = openPanel { body ->
        val box = LinearLayout(ui).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(40), dp(22), dp(28), dp(14))
        }
        val columns = LinearLayout(ui).apply { orientation = LinearLayout.HORIZONTAL }
        val audioList = column(columns, ui.getString(R.string.player_audio))
        columns.addView(View(ui).apply { setBackgroundColor(0x33FFFFFF) }, LinearLayout.LayoutParams(dp(1), ViewGroup.LayoutParams.MATCH_PARENT).apply { setMargins(dp(8), dp(48), dp(32), dp(8)) })
        val textList = column(columns, ui.getString(R.string.player_subtitles))
        box.addView(columns, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        val groups = player.currentTracks.groups
        val params = player.trackSelectionParameters
        // (a track the device can't decode is still listed, greyed, so it's clear why there's no sound)
        val cant = ui.getString(R.string.player_cant_play, Device.thisOne(activity))
        val audioGroups = groups.filter { it.type == C.TRACK_TYPE_AUDIO }
        for (g in audioGroups) for (i in 0 until g.length) {
            val ok = g.isTrackSupported(i)
            val chosen = pickedAudio?.let { it.group == g && it.index == i } ?: (ok && g.isTrackSelected(i))
            option(audioList, trackName(g.getTrackFormat(i), audio = true), chosen, if (ok) null else cant, ok) {
                pickedAudio = Pick(g, i)
                openTracks()
            }
        }
        if (audioGroups.isEmpty()) option(audioList, ui.getString(R.string.player_audio_default), true) {}
        val textGroups = groups.filter { it.type == C.TRACK_TYPE_TEXT }
        val textOff = params.disabledTrackTypes.contains(C.TRACK_TYPE_TEXT) || textGroups.none { it.isSelected }
        option(textList, ui.getString(R.string.player_subtitles_off), pickedText?.let { it.group == null } ?: textOff) {
            pickedText = Pick(null, 0)
            openTracks()
        }
        for (g in textGroups) for (i in 0 until g.length) {
            val ok = g.isTrackSupported(i)
            val chosen = pickedText?.let { it.group == g && it.index == i } ?: (ok && !textOff && g.isTrackSelected(i))
            option(textList, trackName(g.getTrackFormat(i), audio = false), chosen, if (ok) null else cant, ok) {
                pickedText = Pick(g, i)
                openTracks()
            }
        }

        val buttons = LinearLayout(ui).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
        }
        buttons.addView(text(ui.getString(R.string.player_cancel), 14f, WHITE, medium = true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(20), 0, dp(20), 0)
            setBackgroundResource(R.drawable.mc_option)
            setOnClickListener { closePanel() }
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(40)))
        buttons.addView(text(ui.getString(R.string.player_apply), 14f, 0xFF000000.toInt(), medium = true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(24), 0, dp(24), 0)
            setBackgroundResource(R.drawable.mc_apply)
            setOnClickListener {
                pickedAudio?.let { p -> p.group?.let { select(C.TRACK_TYPE_AUDIO, it, p.index) } }
                pickedText?.let { p -> select(C.TRACK_TYPE_TEXT, p.group, p.index) }
                closePanel()
            }
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(40)).apply { marginStart = dp(10) })
        box.addView(buttons, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) })
        body.addView(box, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private fun column(parent: LinearLayout, title: String): LinearLayout {
        val holder = LinearLayout(ui).apply { orientation = LinearLayout.VERTICAL }
        holder.addView(heading(title).apply { setPadding(0, dp(4), 0, dp(12)) })
        val scroll = ScrollView(ui).apply { isVerticalScrollBarEnabled = false }
        val list = LinearLayout(ui).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(list)
        holder.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        parent.addView(holder, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))
        return list
    }

    /** A choice in a list; [enabled] false: shown greyed (a track this device can't play), not pickable */
    private fun option(list: LinearLayout, label: String, selected: Boolean, detail: String? = null, enabled: Boolean = true, onPick: () -> Unit) {
        val row = LinearLayout(ui).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.mc_option)
            setPadding(dp(4), dp(9), dp(10), dp(9))
            if (enabled) setOnClickListener { onPick() } else alpha = 0.4f
        }
        row.addView(ImageView(ui).apply {
            setImageResource(R.drawable.ic_check)
            visibility = if (selected) View.VISIBLE else View.INVISIBLE
        }, LinearLayout.LayoutParams(dp(20), dp(20)))
        val texts = LinearLayout(ui).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(14), 0, 0, 0) }
        texts.addView(text(label, 15f, if (selected) WHITE else GREY, medium = selected))
        if (!detail.isNullOrEmpty()) texts.addView(text(detail, 12f, GREY_DIM).apply { maxLines = 2 })
        row.addView(texts, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        list.addView(row)
    }

    private fun select(type: Int, group: Tracks.Group?, index: Int) {
        val b = player.trackSelectionParameters.buildUpon().clearOverridesOfType(type)
        if (group == null) b.setTrackTypeDisabled(type, true)
        else b.setTrackTypeDisabled(type, false).setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, index))
        player.trackSelectionParameters = b.build()
    }

    private fun trackName(f: Format, audio: Boolean): String {
        val locale = Lang.locale(activity)
        val language = f.language?.takeIf { it.isNotEmpty() && it != "und" }?.let { java.util.Locale.forLanguageTag(it).getDisplayLanguage(locale).replaceFirstChar { c -> c.titlecase(locale) } }
        val name = f.label?.takeIf { it.isNotEmpty() } ?: language ?: ui.getString(if (audio) R.string.player_audio else R.string.player_subtitles)
        val channels = if (audio) when (f.channelCount) {
            1 -> " · " + ui.getString(R.string.track_mono)
            2 -> " · " + ui.getString(R.string.track_stereo)
            6 -> " · 5.1"
            8 -> " · 7.1"
            else -> ""
        } else ""
        val format = if (audio) when (f.sampleMimeType) {
            MimeTypes.AUDIO_AC3 -> " · Dolby Digital"
            MimeTypes.AUDIO_E_AC3, MimeTypes.AUDIO_E_AC3_JOC -> " · Dolby Digital+"
            MimeTypes.AUDIO_TRUEHD -> " · TrueHD"
            MimeTypes.AUDIO_DTS, MimeTypes.AUDIO_DTS_HD, MimeTypes.AUDIO_DTS_EXPRESS -> " · DTS"
            MimeTypes.AUDIO_AAC -> " · AAC"
            MimeTypes.AUDIO_OPUS -> " · Opus"
            MimeTypes.AUDIO_FLAC -> " · FLAC"
            else -> ""
        } else ""
        return name + channels + format
    }

    // Episodes: the season's episodes in a row of cards (picture, title, summary), the one playing outlined
    private fun openEpisodes(): Unit = openPanel { body ->
        val box = LinearLayout(ui).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(22), 0, dp(16))
        }
        box.addView(heading(if (season > 0) ui.getString(R.string.player_season, season) else ui.getString(R.string.player_episodes)).apply {
            setPadding(dp(40), 0, dp(64), dp(16))
        })
        val scroll = HorizontalScrollView(ui).apply { isHorizontalScrollBarEnabled = false }
        val row = LinearLayout(ui).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(40), 0, dp(26), 0)
        }
        scroll.addView(row)
        var current: View? = null
        for (ep in episodes) {
            val card = episodeCard(ep)
            row.addView(card, LinearLayout.LayoutParams(dp(CARD_W), ViewGroup.LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(14) })
            if (ep.current) current = card
        }
        box.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        body.addView(box, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        // the episode playing in view
        current?.let { c -> scroll.post { scroll.scrollTo((c.left - dp(40)).coerceAtLeast(0), 0) } }
    }

    private fun episodeCard(ep: Episode): View {
        val card = LinearLayout(ui).apply {
            orientation = LinearLayout.VERTICAL
            setOnClickListener { if (!ep.current) activity.playEpisode(ep.route) }
        }
        val picture = FrameLayout(ui).apply { setBackgroundResource(R.drawable.mc_card) }
        val number = text(ep.episode.toString(), 28f, 0x66FFFFFF, medium = true).apply { gravity = Gravity.CENTER }
        picture.addView(number, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val image = ImageView(ui).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            clipToOutline = true
            outlineProvider = android.view.ViewOutlineProvider.BACKGROUND
            setBackgroundResource(R.drawable.mc_card)
        }
        picture.addView(image, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        if (ep.thumbnail.isNotEmpty()) {
            image.visibility = View.INVISIBLE
            Thumbs.load(ep.thumbnail, image, dp(CARD_W)) { image.visibility = View.VISIBLE }
        } else image.visibility = View.GONE
        if (ep.current) {
            picture.foreground = ui.getDrawable(R.drawable.mc_card_now)
            picture.addView(text(ui.getString(R.string.player_now_playing), 11f, WHITE, medium = true).apply {
                setBackgroundColor(0xFFE50914.toInt())
                setPadding(dp(6), dp(2), dp(6), dp(2))
            }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP or Gravity.START).apply { setMargins(dp(8), dp(8), 0, 0) })
        }
        card.addView(picture, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(CARD_W * 9 / 16)))
        val name = if (ep.title.isEmpty()) "${ep.episode}" else "${ep.episode}. ${ep.title}"
        card.addView(text(name, 14f, WHITE, medium = true).apply {
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            setPadding(0, dp(8), 0, 0)
        })
        if (ep.overview.isNotEmpty()) card.addView(text(ep.overview, 12f, GREY).apply {
            maxLines = 3
            ellipsize = TextUtils.TruncateAt.END
            setPadding(0, dp(4), 0, 0)
            setLineSpacing(0f, 1.1f)
        })
        return card
    }

    private fun dp(v: Int) = (v * activity.resources.displayMetrics.density).toInt()

    private fun speedText(speed: Float) = "${speed.toString().trimEnd('0').trimEnd('.')}x"

    private fun clock(ms: Long): String {
        val s = ms / 1000
        return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60) else "%d:%02d".format(s / 60, s % 60)
    }

    companion object {
        private const val HIDE_AFTER_MS = 4000L
        private const val CARD_W = 236
        private const val WHITE = 0xFFFFFFFF.toInt()
        private const val GREY = 0xFFB3B3B3.toInt()
        private const val GREY_DIM = 0xFF8C8C8C.toInt()
        private const val GREY_LINE = 0xFF6E6E6E.toInt()
    }
}
