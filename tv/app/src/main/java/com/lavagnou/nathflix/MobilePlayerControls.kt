package com.lavagnou.nathflix

import android.content.Context
import android.graphics.Typeface
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.TimeBar
import java.util.Locale
import kotlin.math.abs

/**
 * The player's controls on a phone or tablet, made like Netflix's app: a tap shows or hides them,
 * a double tap on the left or right skips 10 seconds back or forward, sliding up or down on the left
 * changes the brightness and on the right the volume. Lock keeps stray touches away while watching.
 * A TV keeps Media3's own controls, steered with the remote.
 */
@OptIn(UnstableApi::class)
class MobilePlayerControls(
    private val activity: PlayerActivity,
    private val player: ExoPlayer,
    title: String,
    subtitle: String,
    /** The season's episodes (the one playing marked), for the Episodes panel; empty for a film */
    private val episodes: List<Episode>,
    private val nextLabel: String?,
    private val canPip: Boolean,
) {
    class Episode(val route: String, val episode: Int, val title: String, val overview: String, val current: Boolean)

    val view: View = LayoutInflater.from(activity).inflate(R.layout.player_mobile_controls, null)
    private val controls: View = view.findViewById(R.id.mc_controls)
    private val play: ImageButton = view.findViewById(R.id.mc_play)
    private val timeBar: DefaultTimeBar = view.findViewById(R.id.mc_timebar)
    private val remaining: TextView = view.findViewById(R.id.mc_remaining)
    private val unlock: TextView = view.findViewById(R.id.mc_unlock)
    private val tapBack: TextView = view.findViewById(R.id.mc_tap_back)
    private val tapForward: TextView = view.findViewById(R.id.mc_tap_forward)
    private val level: View = view.findViewById(R.id.mc_level)
    private val levelIcon: ImageView = view.findViewById(R.id.mc_level_icon)
    private val levelBar: ProgressBar = view.findViewById(R.id.mc_level_bar)
    private val panel: View = view.findViewById(R.id.mc_panel)
    private val panelBody: LinearLayout = view.findViewById(R.id.mc_panel_body)

    private val main = Handler(Looper.getMainLooper())
    private val audio = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var locked = false
    private var scrubbing = false
    private var inPip = false

    /** Whether the controls are up (the skip button stays clear of them) */
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
        view.findViewById<TextView>(R.id.mc_title).text = title
        view.findViewById<TextView>(R.id.mc_subtitle).apply {
            text = subtitle
            visibility = if (subtitle.isEmpty()) View.GONE else View.VISIBLE
        }
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
        view.findViewById<View>(R.id.mc_speed).setOnClickListener { openSpeed() }
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
                remaining.text = "-" + clock((player.duration - position).coerceAtLeast(0))
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
            remaining.text = if (duration > 0) "-" + clock((duration - player.currentPosition).coerceAtLeast(0)) else ""
        }
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

    // ---- Panels: Speed, Audio & Subtitles, Episodes

    private fun openPanel(fill: (LinearLayout) -> Unit) {
        hide()
        panelBody.removeAllViews()
        fill(panelBody)
        panel.visibility = View.VISIBLE
    }

    private fun closePanel() {
        panel.visibility = View.GONE
        if (!inPip) showFor()
    }

    /** Back closes an open panel first */
    fun back(): Boolean {
        if (panel.visibility != View.VISIBLE) return false
        closePanel()
        return true
    }

    private fun column(parent: LinearLayout, heading: String, weight: Float = 1f): LinearLayout {
        val scroll = ScrollView(activity)
        parent.addView(scroll, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight))
        val list = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL; setPadding(0, 0, dp(24), dp(24)) }
        scroll.addView(list)
        list.addView(TextView(activity).apply {
            text = heading
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 20f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(8), 0, dp(14))
        })
        return list
    }

    private fun option(list: LinearLayout, label: String, selected: Boolean, detail: String? = null, onPick: () -> Unit) {
        val row = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.mc_option)
            setPadding(dp(6), dp(10), dp(10), dp(10))
            setOnClickListener {
                onPick()
                closePanel()
            }
        }
        row.addView(ImageView(activity).apply {
            setImageResource(R.drawable.ic_check)
            visibility = if (selected) View.VISIBLE else View.INVISIBLE
        }, LinearLayout.LayoutParams(dp(22), dp(22)))
        val texts = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, 0, 0) }
        texts.addView(TextView(activity).apply {
            text = label
            textSize = 16f
            setTextColor(if (selected) 0xFFFFFFFF.toInt() else 0xFFB3B3B3.toInt())
            if (selected) setTypeface(typeface, Typeface.BOLD)
        })
        if (!detail.isNullOrEmpty()) texts.addView(TextView(activity).apply {
            text = detail
            textSize = 13f
            maxLines = 2
            setTextColor(0xFF8C8C8C.toInt())
        })
        row.addView(texts, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        list.addView(row)
    }

    private fun openSpeed() = openPanel { body ->
        val list = column(body, "Playback speed")
        val now = player.playbackParameters.speed
        for (speed in listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f)) {
            val label = if (speed == 1f) "1x (Normal)" else "${speed.toString().trimEnd('0').trimEnd('.')}x"
            option(list, label, abs(now - speed) < 0.01f) { player.setPlaybackSpeed(speed) }
        }
    }

    /** Audio on the left, subtitles on the right, like Netflix's panel */
    private fun openTracks() = openPanel { body ->
        val audioList = column(body, "Audio")
        val textList = column(body, "Subtitles")
        val groups = player.currentTracks.groups
        val params = player.trackSelectionParameters
        val audioGroups = groups.filter { it.type == C.TRACK_TYPE_AUDIO }
        for (g in audioGroups) for (i in 0 until g.length) {
            if (!g.isTrackSupported(i)) continue
            option(audioList, trackName(g.getTrackFormat(i), audio = true), g.isTrackSelected(i)) { select(C.TRACK_TYPE_AUDIO, g, i) }
        }
        if (audioGroups.isEmpty()) option(audioList, "Default", true) {}
        val textGroups = groups.filter { it.type == C.TRACK_TYPE_TEXT }
        val textOff = params.disabledTrackTypes.contains(C.TRACK_TYPE_TEXT) || textGroups.none { it.isSelected }
        option(textList, "Off", textOff) { select(C.TRACK_TYPE_TEXT, null, 0) }
        for (g in textGroups) for (i in 0 until g.length) {
            if (!g.isTrackSupported(i)) continue
            option(textList, trackName(g.getTrackFormat(i), audio = false), !textOff && g.isTrackSelected(i)) { select(C.TRACK_TYPE_TEXT, g, i) }
        }
    }

    private fun select(type: Int, group: Tracks.Group?, index: Int) {
        val b = player.trackSelectionParameters.buildUpon().clearOverridesOfType(type)
        if (group == null) b.setTrackTypeDisabled(type, true)
        else b.setTrackTypeDisabled(type, false).setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, index))
        player.trackSelectionParameters = b.build()
    }

    private fun trackName(f: Format, audio: Boolean): String {
        val language = f.language?.takeIf { it.isNotEmpty() && it != "und" }?.let { Locale.forLanguageTag(it).displayLanguage.replaceFirstChar { c -> c.titlecase() } }
        val name = f.label?.takeIf { it.isNotEmpty() } ?: language ?: if (audio) "Audio" else "Subtitles"
        val channels = if (audio) when (f.channelCount) { 1 -> " · Mono"; 2 -> " · Stereo"; 6 -> " · 5.1"; 8 -> " · 7.1"; else -> "" } else ""
        return name + channels
    }

    private fun openEpisodes() = openPanel { body ->
        val list = column(body, "Episodes")
        for (ep in episodes) {
            option(list, "${ep.episode}. ${ep.title}".trimEnd('.', ' '), ep.current, ep.overview) {
                if (!ep.current) activity.playEpisode(ep.route)
            }
        }
    }

    private fun dp(v: Int) = (v * activity.resources.displayMetrics.density).toInt()

    private fun clock(ms: Long): String {
        val s = ms / 1000
        return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60) else "%d:%02d".format(s / 60, s % 60)
    }

    companion object {
        private const val HIDE_AFTER_MS = 4000L
    }
}
