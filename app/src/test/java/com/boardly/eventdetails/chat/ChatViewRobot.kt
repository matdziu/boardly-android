package com.boardly.eventdetails.chat

import com.boardly.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ChatViewRobot(chatViewModel: ChatViewModel) : BaseViewRobot<ChatViewState>() {

    private val messageSubject = PublishSubject.create<String>()
    private val batchFetchTriggerSubject = PublishSubject.create<String>()
    private val newMessagesListenerToggleSubject = PublishSubject.create<Boolean>()

    private val chatView = object : ChatView {
        override fun render(chatViewState: ChatViewState) {
            renderedStates.add(chatViewState)
        }

        override fun newMessagesListenerToggleEmitter(): Observable<Boolean> = newMessagesListenerToggleSubject

        override fun batchFetchTriggerEmitter(): Observable<String> = batchFetchTriggerSubject

        override fun messageEmitter(): Observable<String> = messageSubject
    }

    init {
        chatViewModel.bind(chatView, "testEventId")
    }

    fun toggleMessagesListening(toggle: Boolean) {
        newMessagesListenerToggleSubject.onNext(toggle)
    }

    fun triggerBatchFetching(fromTimestamp: String) {
        batchFetchTriggerSubject.onNext(fromTimestamp)
    }

    fun sendMessage(message: String) {
        messageSubject.onNext(message)
    }
}