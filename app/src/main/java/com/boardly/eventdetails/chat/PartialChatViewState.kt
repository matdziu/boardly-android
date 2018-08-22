package com.boardly.eventdetails.chat

sealed class PartialChatViewState {

    abstract fun reduce(previousState: ChatViewState): ChatViewState

    class ProgressState : PartialChatViewState() {
        override fun reduce(previousState: ChatViewState): ChatViewState {
            return previousState.copy(progress = true)
        }
    }
}