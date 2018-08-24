package com.boardly.eventdetails.chat

import com.boardly.eventdetails.chat.list.Message

data class ChatViewState(val progress: Boolean = true,
                         val messagesList: List<Message> = listOf())