package com.lavagnou.nathflix

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

/**
 * The player's controls on a TV, made like Netflix's and steered with the remote. Back and Restart at
 * the top left (the name of the one the remote is on shows under it), the show and the episode at the
 * top right; at the bottom the play button, the time played, the bar and the length, then a row with
 * Audio & Subtitles, Episodes and Next Episode. On play, left and right skip 10 seconds; up goes to
 * the top icons (or Skip Intro while it's up), down to the row. While the film plays they go after a
 * few seconds; paused, they stay. The subtitles and the skip button move up above them.
 */
@OptIn(UnstableApi::class)
class TvPlayerControls(
    private val activity: PlayerActivity,
    private val player: ExoPlayer,
    title: String,
    episodeLine: String,
    /** The season's episodes (the one playing marked), for the Episodes panel; empty for a film */
    private val episodes: List<MobilePlayerControls.Episode>,
    private val season: Int,
    hasNext: Boolean,
    /** Skip Intro / Next Episode, the activity's own: it moves up above the controls */
    private val skip: View,
    private val subtitles: View?,
) {
    /** The activity in Nathflix's language, for every text and view made here */
    private val ui: Context = Lang.context(activity, R.style.Theme_Nathflix_Player)

    val view: View = LayoutInflater.from(ui).inflate(R.layout.player_tv_controls, null)
    private val controls: View = view.findViewById(R.id.tc_controls)
    private val bottom: View = view.findViewById(R.id.tc_bottom)
    private val back: ImageButton = view.findViewById(R.id.tc_back)
    private val restart: ImageButton = view.findViewById(R.id.tc_restart)
    private val iconLabel: TextView = view.findViewById(R.id.tc_icon_label)
    private val play: ImageButton = view.findViewById(R.id.tc_play)
    private val elapsed: TextView = view.findViewById(R.id.tc_elapsed)
    private val total: TextView = view.findViewById(R.id.tc_total)
    private val bar: TvTimeBar = view.findViewById(R.id.tc_bar)
    private val panel: FrameLayout = view.findViewById(R.id.tc_panel)
    private val pills: List<TextView>
    // the button a panel was opened from: the remote is back on it when the panel closes
    private var openedFrom: View? = null

    private val main = Handler(Looper.getMainLooper())
    private val onFocus = ContextCompat.getColorStateList(ui, R.color.tc_on_focus)

    /** Whether the controls are up */
    val shown get() = controls.visibility == View.VISIBLE

    /** Whether Audio & Subtitles or Episodes is open */
    val panelOpen get() = panel.visibility == View.VISIBLE

    private val hideLater = Runnable { if (player.isPlaying) hide() }
    private val tick = object : Runnable {
        override fun run() {
            if (shown) update()
            main.postDelayed(this, 500)
        }
    }

    init {
        view.findViewById<TextView>(R.id.tc_title).text = title
        view.findViewById<TextView>(R.id.tc_episode).apply {
            text = episodeLine
            visibility = if (episodeLine.isEmpty()) View.GONE else View.VISIBLE
        }

        for (icon in listOf(back, restart, play)) icon.imageTintList = onFocus
        back.setOnClickListener { activity.leave() }
        restart.setOnClickListener {
            player.seekTo(0)
            player.play()
            update()
            play.requestFocus()
            show()
        }
        for (icon in listOf(back, restart)) {
            icon.nextFocusUpId = icon.id
            icon.nextFocusDownId = R.id.tc_play
            icon.setOnFocusChangeListener { v, has -> nameIcon(v, has) }
        }
        back.nextFocusLeftId = back.id
        back.nextFocusRightId = restart.id
        restart.nextFocusLeftId = back.id
        restart.nextFocusRightId = restart.id

        play.setOnClickListener {
            player.playWhenReady = !player.playWhenReady
            update()
            show()
        }
        play.setOnFocusChangeListener { v, has ->
            grow(v, has, 1.08f)
            bar.thick = has
        }
        // On play: left and right skip 10 seconds, up and down go to the icons and the row
        play.setOnKeyListener { _, code, e ->
            if (e.action != KeyEvent.ACTION_DOWN) return@setOnKeyListener false
            when (code) {
                KeyEvent.KEYCODE_DPAD_LEFT -> { seek(-1); true }
                KeyEvent.KEYCODE_DPAD_RIGHT -> { seek(+1); true }
                KeyEvent.KEYCODE_DPAD_UP -> { (if (skip.visibility == View.VISIBLE && skip.isFocusable) skip else back).requestFocus(); true }
                KeyEvent.KEYCODE_DPAD_DOWN -> { pills.firstOrNull()?.requestFocus(); true }
                else -> false
            }
        }

        val audio = view.findViewById<TextView>(R.id.tc_audio)
        val episodesButton = view.findViewById<TextView>(R.id.tc_episodes)
        val next = view.findViewById<TextView>(R.id.tc_next)
        audio.setOnClickListener { openTracks(it) }
        episodesButton.visibility = if (episodes.size > 1) View.VISIBLE else View.GONE
        episodesButton.setOnClickListener { openEpisodes(it) }
        next.visibility = if (hasNext) View.VISIBLE else View.GONE
        next.setOnClickListener { activity.playNext() }
        pills = listOf(audio, episodesButton, next).filter { it.visibility == View.VISIBLE }
        pills.forEachIndexed { i, pill ->
            // (the icons are small next to the labels)
            pill.compoundDrawablesRelative[0]?.let { icon ->
                icon.setBounds(0, 0, dp(15), dp(15))
                pill.setCompoundDrawablesRelative(icon, null, null, null)
            }
            pill.nextFocusUpId = R.id.tc_play
            pill.nextFocusDownId = pill.id
            pill.nextFocusLeftId = pills[(i - 1).coerceAtLeast(0)].id
            pill.nextFocusRightId = pills[(i + 1).coerceAtMost(pills.lastIndex)].id
            pill.setOnFocusChangeListener { v, has -> grow(v, has, 1.06f) }
        }
        // Skip Intro, lifted over the controls: down comes back to play, up goes to the icons
        skip.nextFocusDownId = R.id.tc_play
        skip.nextFocusUpId = R.id.tc_back

        player.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                update()
                // paused: the controls come up and stay, like Netflix
                if (!playWhenReady && !panelOpen && !activity.isFinishing) show()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying && shown) hideAfterAWhile()
            }
        })
        main.post(tick)
    }

    fun release() = main.removeCallbacksAndMessages(null)

    // ---- Showing and hiding

    /** Up, the remote on play (or where it already is); gone again after a while if the film plays */
    fun show() {
        if (panelOpen) return
        val was = shown
        controls.visibility = View.VISIBLE
        update()
        if (!was) {
            play.requestFocus()
            lift(true)
        }
        hideAfterAWhile()
    }

    /** A key was pressed while they're up: they stay a while longer */
    fun poke() {
        if (shown) hideAfterAWhile()
    }

    private fun hideAfterAWhile() {
        main.removeCallbacks(hideLater)
        if (player.isPlaying) main.postDelayed(hideLater, HIDE_AFTER_MS)
    }

    fun hide() {
        main.removeCallbacks(hideLater)
        if (!shown) return
        controls.visibility = View.GONE
        iconLabel.visibility = View.INVISIBLE
        lift(false)
        // the remote goes back to Skip Intro if it's up, otherwise to the picture
        if (skip.visibility == View.VISIBLE && skip.isFocusable) skip.requestFocus()
        else (view.parent as? View)?.findViewById<View>(R.id.player_view)?.requestFocus()
    }

    /** The remote on play, after Skip Intro went (while the controls are up) */
    fun focusPlay() {
        if (shown) play.requestFocus()
    }

    /** Skip Intro came up while the controls are: above them */
    fun placeSkip() = lift(shown)

    // The subtitles and Skip Intro move up above the controls while they're up, and back down after
    private fun lift(up: Boolean) {
        bottom.post {
            val block = (bottom.height + dp(36)).toFloat()
            skip.animate().translationY(if (up) -(block + dp(14) - dp(56)).coerceAtLeast(0f) else 0f).setDuration(LIFT_MS).start()
            subtitles?.let { s ->
                s.animate().translationY(if (up) -(block + dp(10) - s.height * 0.06f).coerceAtLeast(0f) else 0f).setDuration(LIFT_MS).start()
            }
        }
    }

    private fun update() {
        play.setImageResource(if (player.playWhenReady) R.drawable.ic_pause else R.drawable.ic_play)
        val duration = player.duration.takeIf { it != C.TIME_UNSET && it > 0 } ?: 0L
        bar.set(player.currentPosition, player.bufferedPosition, duration)
        elapsed.text = clock(player.currentPosition)
        total.text = if (duration > 0) clock(duration) else ""
    }

    private fun seek(direction: Int) {
        if (direction > 0) player.seekForward() else player.seekBack()
        update()
        hideAfterAWhile()
    }

    // The name of the top icon the remote is on, centred under it (in capitals, like Netflix)
    private fun nameIcon(icon: View, has: Boolean) {
        if (!has) {
            if (!back.isFocused && !restart.isFocused) iconLabel.visibility = View.INVISIBLE
            return
        }
        iconLabel.text = icon.contentDescription
        iconLabel.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val center = (icon.parent as View).left + icon.left + icon.width / 2f
        iconLabel.translationX = (center - iconLabel.measuredWidth / 2f).coerceAtLeast(0f)
        iconLabel.visibility = View.VISIBLE
    }

    private fun grow(v: View, has: Boolean, scale: Float) {
        val to = if (has) scale else 1f
        v.animate().scaleX(to).scaleY(to).setDuration(120).start()
    }

    // ---- Panels: Audio & Subtitles, Episodes (over the picture, like Netflix's)

    private fun openPanel(from: View, fill: (FrameLayout) -> Unit) {
        openedFrom = from
        main.removeCallbacks(hideLater)
        controls.visibility = View.GONE
        iconLabel.visibility = View.INVISIBLE
        lift(false)
        // Skip Intro stays out of the way (and out of the remote's reach) until the panel closes
        skip.alpha = 0f
        skip.isFocusable = false
        panel.removeAllViews()
        fill(panel)
        panel.visibility = View.VISIBLE
    }

    private fun closePanel() {
        panel.visibility = View.GONE
        panel.removeAllViews()
        skip.alpha = 1f
        skip.isFocusable = true
        show()
        openedFrom?.requestFocus()
    }

    /** Back closes an open panel first */
    fun back(): Boolean {
        if (!panelOpen) return false
        closePanel()
        return true
    }

    private fun text(value: String, size: Float, color: Int, weight: Int = NORMAL) = TextView(ui).apply {
        text = value
        textSize = size
        setTextColor(color)
        typeface = face(weight)
    }

    private fun face(weight: Int): Typeface = when (weight) {
        BOLD -> Typeface.create("sans-serif", Typeface.BOLD)
        MEDIUM -> Typeface.create("sans-serif-medium", Typeface.NORMAL)
        else -> Typeface.create("sans-serif", Typeface.NORMAL)
    }

    // Audio & Subtitles: two lists side by side; a choice applies at once (no Apply button on a TV)
    private class Row(val view: LinearLayout, val check: ImageView, val label: TextView, var selected: Boolean)

    private fun openTracks(from: View) = openPanel(from) { body ->
        body.setBackgroundColor(0xCC000000.toInt())
        val columns = LinearLayout(ui).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(48), 0, dp(80))
        }
        val audioList = column(columns, ui.getString(R.string.player_audio), 0)
        val textList = column(columns, ui.getString(R.string.player_subtitles), dp(80))
        body.addView(columns, FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
        body.addView(text(ui.getString(R.string.player_back_to_video), 12f, 0xB8FFFFFF.toInt(), MEDIUM),
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL).apply { bottomMargin = dp(40) })

        val groups = player.currentTracks.groups
        // (a track the device can't decode is still listed, greyed, so it's clear why there's no sound)
        val cant = ui.getString(R.string.player_cant_play, Device.thisOne(activity))
        val audioRows = mutableListOf<Row>()
        val textRows = mutableListOf<Row>()
        for (g in groups.filter { it.type == C.TRACK_TYPE_AUDIO }) for (i in 0 until g.length) {
            val ok = g.isTrackSupported(i)
            option(audioList, audioRows, PlayerTracks.name(ui, g.getTrackFormat(i), audio = true), ok && g.isTrackSelected(i), if (ok) null else cant, ok) {
                PlayerTracks.select(player, C.TRACK_TYPE_AUDIO, g, i)
            }
        }
        if (audioRows.isEmpty()) option(audioList, audioRows, ui.getString(R.string.player_audio_default), true) {}
        val off = PlayerTracks.textOff(player)
        option(textList, textRows, ui.getString(R.string.player_subtitles_off), off) {
            PlayerTracks.select(player, C.TRACK_TYPE_TEXT, null, 0)
        }
        for (g in groups.filter { it.type == C.TRACK_TYPE_TEXT }) for (i in 0 until g.length) {
            val ok = g.isTrackSupported(i)
            option(textList, textRows, PlayerTracks.name(ui, g.getTrackFormat(i), audio = false), ok && !off && g.isTrackSelected(i), if (ok) null else cant, ok) {
                PlayerTracks.select(player, C.TRACK_TYPE_TEXT, g, i)
            }
        }
        // the remote starts on the audio playing
        val start = audioRows.firstOrNull { it.selected && it.view.isFocusable } ?: audioRows.firstOrNull { it.view.isFocusable } ?: textRows.first()
        start.view.post { start.view.requestFocus() }
    }

    private fun column(parent: LinearLayout, title: String, gap: Int): LinearLayout {
        val holder = LinearLayout(ui).apply { orientation = LinearLayout.VERTICAL }
        holder.addView(text(title, 20f, WHITE, BOLD).apply { setPadding(dp(14), 0, 0, dp(9)) })
        val scroll = ScrollView(ui).apply {
            isVerticalScrollBarEnabled = false
            clipToPadding = false
        }
        val list = LinearLayout(ui).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(list)
        holder.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        parent.addView(holder, LinearLayout.LayoutParams(dp(280), ViewGroup.LayoutParams.WRAP_CONTENT).apply { marginStart = gap })
        return list
    }

    /** A choice in a list; [enabled] false: shown greyed (a track this device can't play), out of the remote's reach */
    private fun option(list: LinearLayout, rows: MutableList<Row>, label: String, selected: Boolean, detail: String? = null, enabled: Boolean = true, onPick: () -> Unit) {
        val view = LinearLayout(ui).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(38)
            setBackgroundResource(R.drawable.tc_row_bg)
            setPadding(dp(14), dp(6), dp(14), dp(6))
            isFocusable = enabled
            if (!enabled) alpha = 0.4f
        }
        val check = ImageView(ui).apply { setImageResource(R.drawable.ic_check) }
        view.addView(check, LinearLayout.LayoutParams(dp(18), dp(18)))
        val texts = LinearLayout(ui).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, 0, 0)
        }
        val name = text(label, 16f, WHITE).apply {
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        texts.addView(name)
        if (!detail.isNullOrEmpty()) texts.addView(text(detail, 12f, GREY).apply { maxLines = 2 })
        view.addView(texts, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        list.addView(view, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(5) })
        val row = Row(view, check, name, selected)
        rows += row
        paint(row)
        view.setOnFocusChangeListener { _, _ -> paint(row) }
        view.setOnClickListener {
            if (row.selected) return@setOnClickListener
            onPick()
            for (r in rows) {
                r.selected = r === row
                paint(r)
            }
        }
    }

    // White on the choice the remote is on; the picked one ticked and bold
    private fun paint(r: Row) {
        val focused = r.view.isFocused
        r.check.visibility = if (r.selected) View.VISIBLE else View.INVISIBLE
        r.check.imageTintList = ColorStateList.valueOf(if (focused) BLACK else WHITE)
        r.label.setTextColor(when {
            focused -> BLACK
            r.selected -> WHITE
            else -> 0xBDFFFFFF.toInt()
        })
        r.label.typeface = face(if (focused || r.selected) BOLD else MEDIUM)
    }

    // Episodes: the season's episodes in a row of cards, the summary of the one the remote is on below
    private fun openEpisodes(from: View) = openPanel(from) { body ->
        body.setBackgroundResource(R.drawable.tc_episodes_scrim)
        val box = LinearLayout(ui).apply {
            orientation = LinearLayout.VERTICAL
            clipChildren = false
            clipToPadding = false
            setPadding(dp(56), 0, 0, dp(40))
        }
        val header = LinearLayout(ui).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(5), 0, dp(56), dp(12))
        }
        header.addView(text(ui.getString(R.string.player_episodes), 22f, WHITE, BOLD))
        if (season > 0) header.addView(text(ui.getString(R.string.player_season, season), 13f, WHITE, MEDIUM).apply {
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.tc_chip)
            setPadding(dp(14), 0, dp(14), 0)
        }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(30)).apply { marginStart = dp(14) })
        box.addView(header)

        val scroll = HorizontalScrollView(ui).apply {
            isHorizontalScrollBarEnabled = false
            clipChildren = false
            clipToPadding = false
        }
        val row = LinearLayout(ui).apply {
            orientation = LinearLayout.HORIZONTAL
            clipChildren = false
            clipToPadding = false
            setPadding(dp(4), dp(8), dp(56), dp(8))
        }
        scroll.addView(row)
        box.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        val overview = text("", 13f, 0xDBFFFFFF.toInt(), MEDIUM).apply {
            minLines = 3
            maxLines = 3
            ellipsize = TextUtils.TruncateAt.END
            setLineSpacing(0f, 1.2f)
            setPadding(dp(5), 0, 0, 0)
        }
        box.addView(overview, LinearLayout.LayoutParams(dp(560), ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(10) })
        body.addView(box, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM))

        var current: View? = null
        for (ep in episodes) {
            val card = episodeCard(ep) { overview.text = ep.overview }
            row.addView(card, LinearLayout.LayoutParams(dp(CARD_W), ViewGroup.LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(14) })
            if (ep.current) current = card
        }
        // the remote starts on the episode playing (the row scrolls to it)
        val start = current ?: row.getChildAt(0)
        start?.post { start.requestFocus() }
    }

    private fun episodeCard(ep: MobilePlayerControls.Episode, focused: () -> Unit): View {
        val card = LinearLayout(ui).apply {
            orientation = LinearLayout.VERTICAL
            isFocusable = true
            setBackgroundResource(R.drawable.tc_card_bg)
            setPadding(dp(5), dp(5), dp(5), dp(9))
            setOnClickListener { if (ep.current) closePanel() else activity.playEpisode(ep.route) }
        }
        val pictureW = CARD_W - 10
        val picture = FrameLayout(ui).apply {
            setBackgroundResource(R.drawable.tc_thumb)
            clipToOutline = true
            outlineProvider = ViewOutlineProvider.BACKGROUND
        }
        picture.addView(text(ep.episode.toString(), 26f, 0x66FFFFFF, MEDIUM).apply { gravity = Gravity.CENTER },
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val image = ImageView(ui).apply { scaleType = ImageView.ScaleType.CENTER_CROP }
        picture.addView(image, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        if (ep.thumbnail.isNotEmpty()) {
            image.visibility = View.INVISIBLE
            Thumbs.load(ep.thumbnail, image, dp(pictureW)) { image.visibility = View.VISIBLE }
        } else image.visibility = View.GONE
        // how far into the episode playing we are, in red along the bottom of its picture
        if (ep.current) {
            val duration = player.duration.takeIf { it != C.TIME_UNSET && it > 0 }
            if (duration != null) {
                val track = FrameLayout(ui).apply { setBackgroundColor(0x47FFFFFF) }
                track.addView(View(ui).apply { setBackgroundColor(RED) },
                    FrameLayout.LayoutParams((dp(pictureW) * (player.currentPosition.toFloat() / duration).coerceIn(0f, 1f)).toInt(), ViewGroup.LayoutParams.MATCH_PARENT))
                picture.addView(track, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(4), Gravity.BOTTOM))
            }
        }
        card.addView(picture, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(pictureW * 9 / 16)))
        val name = if (ep.title.isEmpty()) ui.getString(R.string.player_episode_card, ep.episode, "").removeSuffix(" · ")
        else ui.getString(R.string.player_episode_card, ep.episode, ep.title)
        val title = text(name, 13f, WHITE, BOLD).apply {
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            setPadding(dp(2), dp(7), dp(2), 0)
        }
        card.addView(title)
        val now = if (ep.current) text(ui.getString(R.string.player_now_playing), 12f, GREY, MEDIUM).apply { setPadding(dp(2), dp(2), dp(2), 0) } else null
        now?.let { card.addView(it) }
        card.setOnFocusChangeListener { v, has ->
            grow(v, has, 1.04f)
            title.setTextColor(if (has) BLACK else WHITE)
            now?.setTextColor(if (has) BLACK else GREY)
            if (has) focused()
        }
        return card
    }

    private fun dp(v: Int) = (v * activity.resources.displayMetrics.density).toInt()

    private fun clock(ms: Long): String {
        val s = ms / 1000
        return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s / 60 % 60, s % 60) else "%d:%02d".format(s / 60, s % 60)
    }

    companion object {
        private const val HIDE_AFTER_MS = 5000L
        private const val LIFT_MS = 180L
        private const val CARD_W = 200
        private const val NORMAL = 0
        private const val MEDIUM = 1
        private const val BOLD = 2
        private const val WHITE = 0xFFFFFFFF.toInt()
        private const val BLACK = 0xFF000000.toInt()
        private const val GREY = 0xB8FFFFFF.toInt()
        private const val RED = 0xFFE50914.toInt()
    }
}

/** The TV player's bar: what's played in red over what's loaded, like Netflix's (thicker while play has the remote) */
class TvTimeBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private var position = 0L
    private var buffered = 0L
    private var duration = 0L

    var thick = false
        set(value) {
            field = value
            invalidate()
        }

    fun set(position: Long, buffered: Long, duration: Long) {
        if (position == this.position && buffered == this.buffered && duration == this.duration) return
        this.position = position
        this.buffered = buffered
        this.duration = duration
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val density = resources.displayMetrics.density
        val h = (if (thick) 5f else 4f) * density
        val top = (height - h) / 2
        val r = h / 2
        fun part(fraction: Float, color: Int) {
            if (fraction <= 0f) return
            paint.color = color
            rect.set(0f, top, width * fraction.coerceAtMost(1f), top + h)
            canvas.drawRoundRect(rect, r, r, paint)
        }
        part(1f, 0x52FFFFFF)
        if (duration > 0) {
            part(buffered.toFloat() / duration, 0x38FFFFFF)
            part(position.toFloat() / duration, 0xFFE50914.toInt())
        }
    }
}
