package com.boardly.eventdetails.chat.list

data class Message(val id: String = "",
                   val text: String = "",
                   val type: MessageType,
                   val isSent: Boolean = false,
                   val senderName: String = "",
                   val senderImageUrl: String = "",
                   val timestamp: String = "")

enum class MessageType { SENT, RECEIVED }