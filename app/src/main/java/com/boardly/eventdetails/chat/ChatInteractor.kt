package com.boardly.eventdetails.chat

import com.boardly.eventdetails.chat.list.Message
import com.boardly.eventdetails.chat.models.RawMessage
import com.boardly.eventdetails.chat.network.ChatService
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class ChatInteractor @Inject constructor(private val chatService: ChatService,
                                         initialMessagesList: List<Message>) {

    private var currentMessagesList = initialMessagesList

    fun listenForNewMessages(eventId: String): Observable<PartialChatViewState> {
        return chatService.listenForNewMessages(eventId)
                .map { it.toMessage(chatService.userId) }
                .doOnNext { newMessage -> currentMessagesList = currentMessagesList.filter { it.id != newMessage.id } + newMessage }
                .map { PartialChatViewState.MessagesListChanged(currentMessagesList) }
    }

    fun stopListeningForNewMessages(eventId: String): Observable<PartialChatViewState> {
        return chatService.stopListeningForNewMessages(eventId)
                .map { PartialChatViewState.NewMessagesListenerRemoved() }
    }

    fun fetchChatMessagesBatch(eventId: String, fromTimestamp: String): Observable<PartialChatViewState> {
        return chatService.fetchMessagesBatch(eventId, fromTimestamp)
                .map { it.map { it.toMessage(chatService.userId) } }
                .doOnNext { messagesBatchList ->
                    currentMessagesList = (messagesBatchList.sortedBy { it.timestamp } + currentMessagesList).distinctBy { it.id }
                }
                .map { PartialChatViewState.MessagesListChanged(currentMessagesList) }
    }

    fun sendMessage(message: String, eventId: String, messageId: String = ""): Observable<PartialChatViewState> {
        val rawMessage = RawMessage(
                id = if (messageId.isEmpty()) UUID.randomUUID().toString() else messageId,
                text = message,
                senderId = chatService.userId)

        chatService.sendMessage(rawMessage, eventId)

        currentMessagesList += rawMessage.toMessage(chatService.userId, false)
        return Observable.just(PartialChatViewState.MessagesListChanged(currentMessagesList))
    }
}