package com.example.helloworld.web

import okhttp3.OkHttpClient
import okhttp3.Request

object HttpUtil {

    fun sendOkHttpRequest(url: String, cb: okhttp3.Callback) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(cb)
    }
}