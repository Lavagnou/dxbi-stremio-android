package com.lavagnou.nathflix

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration

/** What Nathflix is running on: a TV (steered with a remote) or a phone or tablet (touch) */
object Device {
    /** A phone or tablet rather than a TV: touch, a page laid out to the screen's own width */
    fun isMobile(context: Context): Boolean {
        val ui = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return ui.currentModeType != Configuration.UI_MODE_TYPE_TELEVISION &&
            !context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }

    /** "tv", "phone" or "tablet", for messages that name the device ("this phone") */
    fun kind(context: Context): String = when {
        !isMobile(context) -> "tv"
        context.resources.configuration.smallestScreenWidthDp >= 600 -> "tablet"
        else -> "phone"
    }

    /** "this phone", "ce téléphone"…: the device in messages, in Nathflix's language */
    fun thisOne(context: Context): String = Lang.text(context, when (kind(context)) {
        "tv" -> R.string.this_tv
        "tablet" -> R.string.this_tablet
        else -> R.string.this_phone
    })

    /** "the phone", "le téléphone"… */
    fun theOne(context: Context): String = Lang.text(context, when (kind(context)) {
        "tv" -> R.string.the_tv
        "tablet" -> R.string.the_tablet
        else -> R.string.the_phone
    })

    /** The device's name in messages: "TV", "phone" or "tablet" */
    fun noun(context: Context): String = when (kind(context)) {
        "tv" -> "TV"
        "tablet" -> "tablet"
        else -> "phone"
    }
}
