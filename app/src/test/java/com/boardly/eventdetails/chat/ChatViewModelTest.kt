package com.boardly.eventdetails.chat

import com.boardly.eventdetails.chat.list.Message
import com.boardly.eventdetails.chat.list.MessageType
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import org.junit.Test

class ChatViewModelTest {

    private val testMessage = Message("testMessage", "this is a test message", MessageType.SENT)
    private val testMessagesBatch = listOf(
            Message("1", "one", MessageType.SENT, true, "", "", "2018-08-24T07:06:18.004Z"),
            Message("2", "two", MessageType.RECEIVED, true, "", "", "2018-08-24T07:06:20.004Z"))
    private val chatInteractor: ChatInteractor = mock {
        on { it.fetchChatMessagesBatch(any(), any()) } doReturn Observable.just(PartialChatViewState.MessagesListChanged(testMessagesBatch))
                .cast(PartialChatViewState::class.java)
        on { it.stopListeningForNewMessages(any()) } doReturn Observable.just(PartialChatViewState.NewMessagesListenerRemoved())
                .cast(PartialChatViewState::class.java)
        on { it.sendMessage(any(), any(), any()) } doReturn Observable.just(PartialChatViewState.MessagesListChanged(listOf(testMessage)))
                .cast(PartialChatViewState::class.java)
        on { it.listenForNewMessages(any()) } doReturn Observable.just(PartialChatViewState.MessagesListChanged(listOf(testMessage)))
                .cast(PartialChatViewState::class.java)
    }
    private val chatViewRobot = ChatViewRobot(ChatViewModel(chatInteractor))

    @Test
    fun whenNewMessageArrivesItIsAddedToMessagesListInViewState() {
        chatViewRobot.toggleMessagesListening(true)
        chatViewRobot.assertViewStates(
                ChatViewState(),
                ChatViewState(
                        progress = false,
                        messagesList = listOf(testMessage)))
    }

    @Test
    fun whenNewMessagesListenerIsDetachedViewStateDoesNotChange() {
        chatViewRobot.toggleMessagesListening(false)
        chatViewRobot.assertViewStates(
                ChatViewState(),
                ChatViewState())
    }

    @Test
    fun whenNewMessageIsSentItIsAddedToMessagesListInViewState() {
        chatViewRobot.sendMessage("this is a test message  ")
        chatViewRobot.assertViewStates(
                ChatViewState(),
                ChatViewState(
                        progress = false,
                        messagesList = listOf(testMessage)))
        verify(chatInteractor).sendMessage("this is a test message", "testEventId")
    }

    @Test
    fun whenBatchIsFetchedItIsAddedToMessagesListInViewState() {
        chatViewRobot.triggerBatchFetching("2018-08-24")
        chatViewRobot.assertViewStates(
                ChatViewState(),
                ChatViewState(
                        progress = false,
                        messagesList = testMessagesBatch)
        )
    }
}