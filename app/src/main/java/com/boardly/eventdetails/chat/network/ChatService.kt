package com.boardly.eventdetails.chat.network

import com.boardly.eventdetails.chat.models.RawMessage
import io.reactivex.Observable

interface ChatService {

    val userId: String

    fun listenForNewMessages(eventId: String): Observable<RawMessage>

    fun stopListeningForNewMessages(eventId: String): Observable<Boolean>

    fun fetchMessagesBatch(eventId: String, fromTimestamp: String): Observable<List<RawMessage>>

    fun sendMessage(rawMessage: RawMessage, eventId: String)
}