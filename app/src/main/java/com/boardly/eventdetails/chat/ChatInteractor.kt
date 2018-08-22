package com.boardly.eventdetails.chat

import com.boardly.eventdetails.chat.network.ChatService
import io.reactivex.Observable
import javax.inject.Inject

class ChatInteractor @Inject constructor(private val chatService: ChatService) {

    fun listenForNewMessages(eventId: String): Observable<PartialChatViewState> {

    }

    fun stopListeningForNewMessages(eventId: String): Observable<PartialChatViewState> {

    }

    fun fetchChatMessagesBatch(eventId: String, fromTimestamp: String): Observable<PartialChatViewState> {

    }

    fun sendMessage(message: String, eventId: String): Observable<PartialChatViewState> {

    }
}