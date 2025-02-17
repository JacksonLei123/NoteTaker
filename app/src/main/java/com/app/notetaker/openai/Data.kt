package com.app.notetaker.openai

@kotlinx.serialization.Serializable
data class ChatRequest(val model: String, val messages: List<Message>)

@kotlinx.serialization.Serializable
data class Message(val role: String, val content: String)

@kotlinx.serialization.Serializable
data class ChatResponse(val choices: List<Choice>)

@kotlinx.serialization.Serializable
data class Choice(val message: Message)
