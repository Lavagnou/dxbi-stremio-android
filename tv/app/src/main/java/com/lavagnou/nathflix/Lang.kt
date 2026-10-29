package com.lavagnou.nathflix

import android.content.Context
import android.content.res.Configuration
import android.view.ContextThemeWrapper
import java.util.Locale

/**
 * The language Nathflix's own screens use (the player, messages, update prompts): the one picked for
 * Stremio's interface, which the page passes on (Bridge.setLanguage). French or English; before the
 * page has said, the device's language decides.
 */
object Lang {
    private const val PREFS = "nathflix-lang"

    fun set(context: Context, lang: String) {
        val value = if (lang == "fr") "fr" else "en"
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("lang", value).apply()
    }

    fun get(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("lang", null)
            ?: if (Locale.getDefault().language == "fr") "fr" else "en"

    fun locale(context: Context): Locale = Locale.forLanguageTag(get(context))

    /** [base] (an activity) with its texts in Nathflix's language; [theme] for inflating layouts */
    fun context(base: Context, theme: Int = 0): Context {
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale(base))
        if (theme != 0) return ContextThemeWrapper(base, theme).apply { applyOverrideConfiguration(config) }
        return base.createConfigurationContext(config)
    }

    /** A text from strings.xml in Nathflix's language */
    fun text(context: Context, id: Int, vararg args: Any): String = context(context).getString(id, *args)
}
