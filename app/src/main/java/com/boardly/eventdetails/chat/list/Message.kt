package com.boardly.eventdetails.chat.list

data class Message(val id: String = "",
                   val text: String = "",
                   val type: MessageType)

enum class MessageType { SENT, RECEIVED }