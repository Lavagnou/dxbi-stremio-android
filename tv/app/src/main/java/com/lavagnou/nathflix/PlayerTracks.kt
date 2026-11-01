package com.lavagnou.nathflix

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi

/** Audio and subtitle tracks, as both players' Audio & Subtitles panels list and pick them */
@OptIn(UnstableApi::class)
object PlayerTracks {

    /** [group] null: that kind of track is switched off (subtitles Off) */
    fun select(player: Player, type: Int, group: Tracks.Group?, index: Int) {
        val b = player.trackSelectionParameters.buildUpon().clearOverridesOfType(type)
        if (group == null) b.setTrackTypeDisabled(type, true)
        else b.setTrackTypeDisabled(type, false).setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, index))
        player.trackSelectionParameters = b.build()
    }

    /** The track's language (or its own label), with its channels and format for audio: "English · 5.1 · Dolby Digital+" */
    fun name(ui: Context, f: Format, audio: Boolean): String {
        val locale = Lang.locale(ui)
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

    /** Whether subtitles are off right now (switched off, or none picked) */
    fun textOff(player: Player): Boolean =
        player.trackSelectionParameters.disabledTrackTypes.contains(C.TRACK_TYPE_TEXT) ||
            player.currentTracks.groups.none { it.type == C.TRACK_TYPE_TEXT && it.isSelected }
}
