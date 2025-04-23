package com.app.notetaker.openai

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

fun createChatCompletion(apiKey: String, requestBody: ChatRequest): ChatResponse? {
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val gson = Gson()
    val json = gson.toJson(requestBody)
    val request = Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")
        .addHeader("Authorization", "Bearer $apiKey")
        .post(json.toRequestBody(mediaType))
        .build()

    Client.getHTTPClient().newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            println("Request failed: ${response.code} - ${response.body?.string()}")
            return null
        }
        val responseBody = response.body?.string()
        return gson.fromJson(responseBody, ChatResponse::class.java)
    }
}

fun chatCompletion(scanText: String): Flow<ChatResponse> = flow {
    val apiKey = API_KEY
    val request = ChatRequest(
        model = "gpt-3.5-turbo",
        messages = listOf(Message(role = "user", content = "You are an assistant note taker. Generate a summary outline of the following text: " +
                scanText + ". Please make sure to include indentations and formatting. The summary can be as long as needed"))
    )

    val response = createChatCompletion(apiKey, request)
    if (response != null) {
        emit(response)
    }
}