package com.app.notetaker.openai

import androidx.room.RoomDatabase
import okhttp3.OkHttpClient

abstract class Client {
    companion object {
        fun getHTTPClient(): OkHttpClient {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer YOUR_API_KEY")
                        .build()
                    chain.proceed(request)
                }
                .build()
            return client
        }
    }
}

