package com.boardly.eventdetails.chat

import com.boardly.eventdetails.chat.list.Message

sealed class PartialChatViewState {

    abstract fun reduce(previousState: ChatViewState): ChatViewState

    data class MessagesListChanged(private val newMessagesList: List<Message>) : PartialChatViewState() {
        override fun reduce(previousState: ChatViewState): ChatViewState {
            return previousState.copy(
                    progress = false,
                    messagesList = newMessagesList)
        }
    }

    class NewMessagesListenerRemoved : PartialChatViewState() {
        override fun reduce(previousState: ChatViewState): ChatViewState {
            return previousState
        }
    }
}