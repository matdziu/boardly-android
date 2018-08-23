package com.boardly.eventdetails.chat.network

import com.boardly.base.BaseServiceImpl
import com.boardly.eventdetails.chat.models.RawMessage
import io.reactivex.Observable

class ChatServiceImpl : ChatService, BaseServiceImpl() {

    override val userId: String = currentUserId

    override fun listenForNewMessages(eventId: String): Observable<RawMessage> {

    }

    override fun stopListeningForNewMessages(eventId: String): Observable<Boolean> {

    }

    override fun fetchMessagesBatch(eventId: String, fromTimestamp: String): Observable<List<RawMessage>> {

    }

    override fun sendMessage(rawMessage: RawMessage, eventId: String) {

    }
}