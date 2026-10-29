package com.lavagnou.nathflix

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * Keeps a film playing with the screen off or Nathflix in the background (phones and tablets), with
 * play/pause in the notification and on the lock screen. The player itself stays [PlayerActivity]'s:
 * the activity hands its session over in [session], and this service only keeps it going.
 */
@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    // Nathflix swiped away from the recent apps: nothing keeps playing unless it already was
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = session?.player
        if (player == null || !player.playWhenReady) stopSelf()
    }

    companion object {
        /** The playing film's session, while the player is open (set and cleared by PlayerActivity) */
        @Volatile
        var session: MediaSession? = null
    }
}
