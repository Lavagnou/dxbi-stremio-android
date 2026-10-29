package com.lavagnou.nathflix

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * The one OkHttp client the app's requests share (each caller adjusts its timeouts with newBuilder()),
 * so they share its connections and threads instead of each opening its own
 */
object Http {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
    }
}
