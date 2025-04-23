package com.app.notetaker.openai

import okhttp3.OkHttpClient

abstract class Client {
    companion object {
        fun getHTTPClient(): OkHttpClient {
            val client = OkHttpClient.Builder().build()
            return client
        }
    }
}

