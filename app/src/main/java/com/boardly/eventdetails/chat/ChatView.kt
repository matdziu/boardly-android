package com.boardly.eventdetails.chat

import io.reactivex.Observable

interface ChatView {

    fun newMessagesListenerToggleEmitter(): Observable<Boolean>

    fun batchFetchTriggerEmitter(): Observable<String>

    fun messageEmitter(): Observable<String>

    fun render(chatViewState: ChatViewState)
}