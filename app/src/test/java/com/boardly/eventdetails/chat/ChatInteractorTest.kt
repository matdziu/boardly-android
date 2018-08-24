package com.boardly.eventdetails.chat

import com.boardly.eventdetails.chat.list.Message
import com.boardly.eventdetails.chat.list.MessageType
import com.boardly.eventdetails.chat.models.RawMessage
import com.boardly.eventdetails.chat.network.ChatService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class ChatInteractorTest {

    private val testRawMessage = RawMessage("testMessageId", "testMessage", "testSenderId")
    private val testMessagesBatch = listOf(
            RawMessage("1", "one", "first", "2018-08-24T07:06:18.004Z"),
            RawMessage("2", "two", "second", "2018-08-24T07:06:20.004Z"))
    private val initialMessagesList = listOf(
            Message("initial", "initial text", MessageType.SENT, true))
    private val chatService: ChatService = mock {
        on { it.userId } doReturn "testSenderId"
        on { it.listenForNewMessages(any()) } doReturn Observable.just(testRawMessage)
        on { it.stopListeningForNewMessages(any()) } doReturn Observable.just(true)
        on { it.fetchMessagesBatch(any(), any()) } doReturn Observable.just(testMessagesBatch)
    }
    private val chatInteractor = ChatInteractor(chatService, initialMessagesList)

    @Test
    fun testNewMessageArrival() {
        chatInteractor.listenForNewMessages("testEventId").test()
                .assertValue(PartialChatViewState.MessagesListChanged(listOf(
                        Message("initial", "initial text", MessageType.SENT, true),
                        Message("testMessageId", "testMessage", MessageType.SENT, true))))
    }

    @Test
    fun testRemovingNewMessagesListener() {
        chatInteractor.stopListeningForNewMessages("testEventId").test()
                .assertValue { it is PartialChatViewState.NewMessagesListenerRemoved }
    }

    @Test
    fun testSuccessfulMessageSending() {
        chatInteractor.sendMessage("this is a test message", "testEventId", "testMessageId")
                .test()
                .assertValue(PartialChatViewState.MessagesListChanged(listOf(
                        Message("initial", "initial text", MessageType.SENT, true),
                        Message("testMessageId", "this is a test message", MessageType.SENT, false))))
    }

    @Test
    fun testChatMessageBatchArrival() {
        chatInteractor.fetchChatMessagesBatch("testEventId", "2018-08-24")
                .test()
                .assertValue(PartialChatViewState.MessagesListChanged(listOf(
                        Message("1", "one", MessageType.RECEIVED, true, "", "", "2018-08-24T07:06:18.004Z"),
                        Message("2", "two", MessageType.RECEIVED, true, "", "", "2018-08-24T07:06:20.004Z"),
                        Message("initial", "initial text", MessageType.SENT, true))))
    }
}