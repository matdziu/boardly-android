package com.boardly.eventdetails.chat.network

import com.boardly.base.BaseServiceImpl
import com.boardly.eventdetails.chat.models.RawMessage
import io.reactivex.Observable

class ChatServiceImpl : ChatService, BaseServiceImpl() {

    override val userId: String = currentUserId

    override fun listenForNewMessages(eventId: String): Observable<RawMessage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}