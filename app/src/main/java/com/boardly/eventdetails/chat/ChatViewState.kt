package com.boardly.eventdetails.chat

import com.boardly.eventdetails.chat.list.Message

data class ChatViewState(val progress: Boolean = false,
                         val messagesList: List<Message> = listOf())