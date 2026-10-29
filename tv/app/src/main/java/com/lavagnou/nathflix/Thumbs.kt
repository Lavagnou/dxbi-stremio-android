package com.lavagnou.nathflix

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.widget.ImageView
import okhttp3.Request
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Episode pictures for the phone player's Episodes panel: fetched a few at a time, shrunk to the
 * card's size, and kept in memory while Nathflix runs (opening the panel again shows them at once).
 */
object Thumbs {
    private val http by lazy {
        Http.client.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build()
    }
    private val pool = Executors.newFixedThreadPool(3)
    private val main = Handler(Looper.getMainLooper())
    // about 8 MB of pictures
    private val cache = object : LruCache<String, Bitmap>(8 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }

    /** Puts the picture at [url] in [view] (about [width] pixels wide), then calls [shown] */
    fun load(url: String, view: ImageView, width: Int, shown: () -> Unit) {
        view.tag = url
        cache.get(url)?.let {
            view.setImageBitmap(it)
            shown()
            return
        }
        pool.execute {
            val bitmap = try {
                http.newCall(Request.Builder().url(url).build()).execute().use { response ->
                    if (!response.isSuccessful) null else decode(response.body!!.bytes(), width)
                }
            } catch (_: Exception) { null } ?: return@execute
            cache.put(url, bitmap)
            main.post {
                // the card may show another episode by now
                if (view.tag == url) {
                    view.setImageBitmap(bitmap)
                    shown()
                }
            }
        }
    }

    private fun decode(bytes: ByteArray, width: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= width) sample *= 2
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
    }
}
