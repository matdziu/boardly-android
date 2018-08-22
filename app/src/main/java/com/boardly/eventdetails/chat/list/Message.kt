package com.boardly.eventdetails.chat.list

data class Message(val messageId: String = "",
                   val messageType: MessageType)

enum class MessageType { SENT, RECEIVED }