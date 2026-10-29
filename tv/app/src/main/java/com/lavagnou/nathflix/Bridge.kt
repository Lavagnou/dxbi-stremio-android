package com.lavagnou.nathflix

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * What the page (nathflix-tv.js) can ask the app for: the plugin, its settings, and playback.
 *
 * Android hands this object to every frame in the page, embedded YouTube trailers included, so
 * each call carries [TOKEN]: it's written into nathflix-tv.js, which only runs on Stremio's own page.
 */
class Bridge(private val activity: MainActivity) {

    private val prefs = activity.getSharedPreferences("plugin-settings", Context.MODE_PRIVATE)

    private fun allowed(token: String): Boolean =
        MessageDigest.isEqual(token.toByteArray(), TOKEN.toByteArray())

    /** The Nathflix plugin: the one built in, or a newer one it has updated itself to */
    @JavascriptInterface
    fun pluginSource(token: String): String = if (!allowed(token)) "" else try {
        Updates.forTv(Updates.pluginSource(activity))
    } catch (_: Exception) {
        ""
    }

    /** A plugin's saved settings, as a JSON object (what Stremio Enhanced keeps in <plugin>.plugin.json) */
    @JavascriptInterface
    fun getSettings(token: String, plugin: String): String =
        if (!allowed(token)) "{}" else prefs.getString(plugin, "{}") ?: "{}"

    @JavascriptInterface
    fun saveSetting(token: String, plugin: String, key: String, valueJson: String) {
        if (!allowed(token)) return
        synchronized(this) {
            val all = try { JSONObject(prefs.getString(plugin, "{}") ?: "{}") } catch (_: Exception) { JSONObject() }
            val value: Any? = try { JSONObject("{\"v\":$valueJson}").opt("v") } catch (_: Exception) { valueJson }
            all.put(key, value ?: JSONObject.NULL)
            prefs.edit().putString(plugin, all.toString()).apply()
        }
    }

    @JavascriptInterface
    fun play(token: String, json: String) {
        if (!allowed(token)) return
        activity.runOnUiThread { activity.play(json) }
    }

    /** The stream the native player has right now (the page lost track of it if it restarted), or "" */
    @JavascriptInterface
    fun activePlayback(token: String): String = if (!allowed(token)) "" else PlaybackBus.activePlayback ?: ""

    /** How the native player closed while the page was restarting (so it couldn't be told), or "" */
    @JavascriptInterface
    fun takePendingClose(token: String): String {
        if (!allowed(token)) return ""
        return PlaybackBus.takePendingClose() ?: ""
    }

    @JavascriptInterface
    fun openExternal(token: String, url: String) {
        if (!allowed(token)) return
        activity.runOnUiThread { activity.openExternal(url) }
    }

    @JavascriptInterface
    fun toast(token: String, text: String) {
        if (!allowed(token)) return
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_LONG).show() }
    }

    /** Looks for streaming servers on the network; the page hears back through __nathflixTv.serversFound */
    @JavascriptInterface
    fun findServers(token: String) {
        if (!allowed(token)) return
        ServerFinder.find { urls ->
            activity.js("window.__nathflixTv && window.__nathflixTv.serversFound && window.__nathflixTv.serversFound(${org.json.JSONArray(urls)})")
        }
    }

    /**
     * Starts playing a torrent on the TV itself (no streaming server needed) and returns the address
     * the player reads it from, or "" when it can't. [json]: { infoHash, fileIdx, trackers[], name }
     */
    @JavascriptInterface
    fun torrentUrl(token: String, json: String): String {
        if (!allowed(token)) return ""
        return try {
            val o = JSONObject(json)
            val trackers = o.optJSONArray("trackers")?.let { a -> (0 until a.length()).map { a.optString(it) }.filter { it.isNotEmpty() } } ?: emptyList()
            TorrentEngine.open(activity.applicationContext, o.getString("infoHash"), o.optInt("fileIdx", -1), trackers, o.optString("name"))
        } catch (_: Exception) {
            ""
        }
    }

    /** The device, for problem reports: model, Android, WebView, memory, the torrent engine's state */
    @JavascriptInterface
    fun deviceInfo(token: String): String {
        if (!allowed(token)) return "{}"
        return try {
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val mem = android.app.ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
            val webView = try { androidx.webkit.WebViewCompat.getCurrentWebViewPackage(activity)?.let { "${it.packageName} ${it.versionName}" } } catch (_: Exception) { null }
            JSONObject()
                .put("app", BuildConfigVersion.name(activity))
                .put("device", "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
                .put("android", "${android.os.Build.VERSION.RELEASE} (SDK ${android.os.Build.VERSION.SDK_INT})")
                .put("abis", android.os.Build.SUPPORTED_ABIS.joinToString(","))
                .put("webView", webView ?: "unknown")
                .put("memoryMB", JSONObject().put("total", mem.totalMem / 1048576).put("available", mem.availMem / 1048576).put("low", mem.lowMemory))
                .put("phone", isPhone())
                .put("torrent", TorrentEngine.status() ?: "idle")
                .toString()
        } catch (e: Exception) {
            JSONObject().put("error", e.message ?: "?").toString()
        }
    }

    /** A phone or tablet rather than a TV: touch, and a page laid out to the screen's width */
    @JavascriptInterface
    fun isPhone(): Boolean = Device.isMobile(activity)

    /** "tv", "phone" or "tablet", for messages that name the device */
    @JavascriptInterface
    fun deviceKind(): String = Device.kind(activity)

    @JavascriptInterface
    fun version(): String = BuildConfigVersion.name(activity)

    companion object {
        /** Fresh for every launch; replaces [PLACEHOLDER] in nathflix-tv.js */
        val TOKEN: String = ByteArray(24).also { SecureRandom().nextBytes(it) }.joinToString("") { "%02x".format(it) }
        const val PLACEHOLDER = "__NATHFLIX_TV_TOKEN__"
    }
}
