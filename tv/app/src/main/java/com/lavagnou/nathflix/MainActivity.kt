package com.lavagnou.nathflix

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import org.json.JSONObject
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

/**
 * Nathflix for Android: Stremio's web app with Nathflix built in, steered with a TV's remote or by
 * touch on a phone or tablet. Streams play in [PlayerActivity]; this screen stays alive underneath so
 * Stremio keeps the watch progress.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var web: WebView
    private var injectedAtStart = false
    private var lastBack = 0L
    private var playbackListener: PlaybackBus.Listener? = null
    private var intro: Intro? = null
    // A phone or tablet (touch, the page at the screen's own width) rather than a TV
    private val mobile by lazy { Device.isMobile(this) }
    private val debuggable by lazy { applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0 }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // A TV has no screen timeout worth keeping while browsing; a phone does (the player keeps it on)
        if (!mobile) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setUpSystemBars()

        if (!webViewIsRecentEnough()) return

        // Debug builds can be inspected from a computer (chrome://inspect)
        if (debuggable) WebView.setWebContentsDebuggingEnabled(true)
        web = WebView(this)
        val root = FrameLayout(this)
        root.setBackgroundColor(getColor(R.color.background))
        root.addView(web, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        setContentView(root)
        // Phones and tablets: the page sits between the status and navigation bars, clear of a notch,
        // and above the keyboard while it's open (so the box being typed in stays in sight)
        if (mobile) ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val clear = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime())
            view.setPadding(clear.left, clear.top, clear.right, clear.bottom)
            WindowInsetsCompat.CONSUMED
        }
        // Only when Nathflix is opened, not when the screen is rebuilt (after a page crash, a turned phone)
        if (savedInstanceState == null) intro = Intro(this, root).also { it.start() }
        web.setBackgroundColor(getColor(R.color.background))
        web.isFocusable = true
        web.isFocusableInTouchMode = true

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(false)
            builtInZoomControls = false
            // A desktop browser's identity (Stremio's web app is made for one), plus Nathflix's mark:
            // nathflix-tv/ on a TV, nathflix-mobile/ on a phone or tablet (the plugin adapts to each)
            userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/${chromeVersion()} Safari/537.36 ${if (mobile) "nathflix-mobile" else "nathflix-tv"}/${BuildConfigVersion.name(this@MainActivity)}"
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(web, true)

        web.addJavascriptInterface(Bridge(this), "NathflixTV")
        val startScript = assets.open("nathflix-tv.js").bufferedReader().use { it.readText() }.replace(Bridge.PLACEHOLDER, Bridge.TOKEN)
        if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
            WebViewCompat.addDocumentStartJavaScript(web, startScript, setOf(STREMIO_ORIGIN))
            injectedAtStart = true
        }

        web.webChromeClient = object : WebChromeClient() {
            // A video that hasn't drawn its first frame shows WebView's grey play button: show nothing
            override fun getDefaultVideoPoster(): Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }
        web.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
                // Older WebViews can't run a script before the page: run it as early as possible instead
                if (!injectedAtStart && url != null && url.startsWith(STREMIO_ORIGIN)) view.evaluateJavascript(startScript, null)
            }

            // The page's engine crashed, or Android took its memory back (TVs with 1.5–2 GB): without
            // this, Android closes Nathflix straight back to the home screen
            override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                recoverFromCrash(detail.didCrash())
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url
                if (url.host == "web.stremio.com") return false
                if (url.scheme == "stremio") {
                    openStremioLink(url)
                    return true
                }
                // Everything else leaves the app (sign-in pages, links in descriptions…) if something can open it
                openExternal(url.toString())
                return true
            }
        }

        playbackListener = object : PlaybackBus.Listener {
            override fun onProgress(positionMs: Long, durationMs: Long, paused: Boolean) {
                js("window.__nathflixTv && window.__nathflixTv.onProgress($positionMs, $durationMs, $paused)")
            }

            // The page takes the details from PlaybackBus (a restarted page picks them up as it loads)
            override fun onClosed() {
                js("window.__nathflixTv && window.__nathflixTv.playerClosed && window.__nathflixTv.playerClosed()")
            }
        }

        PlaybackBus.listener = playbackListener

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = goBack()
        })

        val link = intent?.data
        if (link?.scheme == "stremio") openStremioLink(link) else web.loadUrl(STREMIO_URL)
        web.requestFocus()

        // A test build (Nathflix Dev) runs the plugin it was built with, and is never offered a release
        if (!debuggable) {
            Updates.checkPlugin(this)
            Updates.checkApp(this)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // The "update your web engine" message is showing instead of the page
        if (!::web.isInitialized) return
        intent.data?.takeIf { it.scheme == "stremio" }?.let { openStremioLink(it) }
    }

    override fun onResume() {
        super.onResume()
        setUpSystemBars()
        if (::web.isInitialized) web.requestFocus()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Any key skips the intro (and isn't passed on to the page underneath)
        intro?.takeIf { it.showing }?.let {
            if (event.action == KeyEvent.ACTION_UP) it.skip()
            return true
        }
        // Play/pause keys on the Home screen do nothing useful in the page: keep them from scrolling it
        if (event.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) return true
        return super.dispatchKeyEvent(event)
    }

    /** The page decides first (closing a card or menu, going back a screen); on Home, Back twice leaves. */
    private fun goBack() {
        web.evaluateJavascript("(window.__nathflixTv && window.__nathflixTv.back()) ? 1 : 0") { handled ->
            if (handled == "1") return@evaluateJavascript
            val now = System.currentTimeMillis()
            if (now - lastBack < 2500) finish()
            else {
                lastBack = now
                Toast.makeText(this, "Press Back again to leave Nathflix", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Start the page again in a lighter way: the intro film is the heaviest moment of a start, so it's
     * switched off (it can be switched back on in Nathflix's settings). A second crash soon after says so.
     */
    private fun recoverFromCrash(crashed: Boolean) {
        val state = getSharedPreferences("app", MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val again = now - state.getLong("lastPageCrash", 0) < 3 * 60_000
        state.edit().putLong("lastPageCrash", now).apply()
        android.util.Log.w("nathflix", "page engine gone (crashed=$crashed, again=$again)")

        val settings = getSharedPreferences("plugin-settings", MODE_PRIVATE)
        val nathflix = try { JSONObject(settings.getString("nathflix", "{}") ?: "{}") } catch (_: Exception) { JSONObject() }
        nathflix.put("splash", false)
        settings.edit().putString("nathflix", nathflix.toString()).commit()

        (web.parent as? ViewGroup)?.removeView(web)
        web.destroy()
        if (again) {
            setContentView(View(this))
            AlertDialog.Builder(this)
                .setTitle("Nathflix keeps stopping")
                .setMessage("Nathflix's screen stopped twice in a row on this ${Device.noun(this)}, usually because it's short of memory. Closing other apps (or restarting the ${Device.noun(this)}) helps. Try again?")
                .setCancelable(false)
                .setPositiveButton("Try again") { _, _ -> recreate() }
                .setNegativeButton("Close") { _, _ -> finish() }
                .show()
        } else {
            recreate()
        }
    }

    fun play(json: String) {
        PlaybackBus.started(json)
        startActivity(Intent(this, PlayerActivity::class.java).putExtra(PlayerActivity.EXTRA_STREAM, json))
    }

    fun openExternal(url: String) {
        val uri = Uri.parse(url)
        // YouTube (trailers): the TV's YouTube app, asked for by name. Google TV hands a plain YouTube
        // link to a "no browser" stub instead of the app
        if (uri.host?.endsWith("youtube.com") == true || uri.host == "youtu.be") {
            for (pkg in listOf("com.google.android.youtube.tv", "com.amazon.firetv.youtube", "com.google.android.youtube")) {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, uri).setPackage(pkg))
                    return
                } catch (_: Exception) { /* not on this device: the next one */ }
            }
        }
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: Exception) {
            Toast.makeText(this, "No app on this ${Device.noun(this)} can open that link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openStremioLink(uri: Uri) {
        // stremio://host/path/manifest.json → install the addon through Stremio's Addons screen
        val manifest = uri.toString().replaceFirst("stremio://", "https://")
        web.loadUrl("$STREMIO_URL#/addons?addon=" + Uri.encode(manifest))
    }

    /** Whether the page has a screen to show yet (what the intro waits for before fading out) */
    fun pageReady(answer: (Boolean) -> Unit) {
        if (!::web.isInitialized) return answer(true)
        web.evaluateJavascript("!!document.querySelector('$PAGE_READY')") { answer(it == "true") }
    }

    fun js(code: String) {
        if (::web.isInitialized) web.post { web.evaluateJavascript(code, null) }
    }

    /** A TV shows the page full screen; a phone or tablet keeps its bars (light icons on the dark page) */
    private fun setUpSystemBars() {
        // (the theme is full screen, which would also keep a phone's status bar hidden)
        if (mobile) window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            if (mobile) {
                show(WindowInsetsCompat.Type.systemBars())
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            } else {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    private fun chromeVersion(): String =
        WebViewCompat.getCurrentWebViewPackage(this)?.versionName?.substringBefore(" ") ?: "120.0.0.0"

    /**
     * Stremio's web app needs a modern WebView; old TVs that never updated it get told how. Only a
     * version we can actually read counts as too old (Fire TV's own WebView names itself differently).
     */
    private fun webViewIsRecentEnough(): Boolean {
        val name = WebViewCompat.getCurrentWebViewPackage(this)?.versionName ?: return true
        val major = Regex("""^(\d+)\.""").find(name)?.groupValues?.get(1)?.toIntOrNull() ?: return true
        if (major >= MIN_WEBVIEW) return true
        val fireTv = Build.MANUFACTURER.equals("Amazon", ignoreCase = true)
        setContentView(View(this))
        val builder = AlertDialog.Builder(this)
            .setTitle(if (fireTv) "Update your Fire TV" else "Update Android System WebView")
            .setCancelable(false)
        if (fireTv) {
            builder.setMessage("Nathflix needs a newer web engine than this Fire TV has ($major). Update it under Settings → My Fire TV → About → Check for Updates, then open Nathflix again.")
                .setPositiveButton("Close") { _, _ -> finish() }
        } else {
            builder.setMessage("Nathflix needs Android System WebView $MIN_WEBVIEW or newer (this ${Device.noun(this)} has $major). Update it from the Play Store (search \"Android System WebView\"), then open Nathflix again.")
                .setPositiveButton("Open Play Store") { _, _ ->
                    openExternal("market://details?id=com.google.android.webview")
                    finish()
                }
                .setNegativeButton("Close") { _, _ -> finish() }
        }
        builder.show()
        return false
    }

    override fun onDestroy() {
        // recreate() can bring up the new screen before this one is gone: leave its listener alone
        if (PlaybackBus.listener === playbackListener) PlaybackBus.listener = null
        if (::web.isInitialized) web.destroy()
        super.onDestroy()
    }

    companion object {
        const val STREMIO_ORIGIN = "https://web.stremio.com"
        const val STREMIO_URL = "https://web.stremio.com/"
        const val MIN_WEBVIEW = 100
        // SPLASH_READY in nathflix.plugin.js (Home's rows, a details page, Settings…)
        private const val PAGE_READY = """[class*="meta-item-container"], [class*="metadetails-container"], [class*="player-container"], [class*="settings-content"], [class*="addons-content"]"""
    }
}
