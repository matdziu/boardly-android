package com.boardly.myevents

import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.home.models.JoinEventData
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class MyEventsViewModelTest {

    private val testAcceptedEventList = listOf(Event("1", "TestEvent", "testGameId", type = EventType.ACCEPTED))
    private val testInterestingEventList = listOf(Event("2", "TestInterestingEvent", "testGameId2", type = EventType.DEFAULT))
    private val myEventsInteractor: MyEventsInteractor = mock {
        on { it.fetchEvents() } doReturn Observable.just(PartialMyEventsViewState.EventsFetchedState(testAcceptedEventList, listOf(), listOf(), testInterestingEventList))
                .cast(PartialMyEventsViewState::class.java)
        on { it.joinEvent(any()) } doReturn Observable.just(PartialMyEventsViewState.JoinRequestSent(false))
                .cast(PartialMyEventsViewState::class.java)
                .startWith(PartialMyEventsViewState.JoinRequestSent())
    }
    private val myEventsViewModel = MyEventsViewModel(myEventsInteractor, mock())
    private val myEventsViewRobot = MyEventsViewRobot(myEventsViewModel)

    @Test
    fun testSuccessfulEventFetchingWithProgressBar() {
        myEventsViewRobot.triggerEventsFetching(true)

        myEventsViewRobot.assertViewStates(
                MyEventsViewState(),
                MyEventsViewState(progress = true),
                MyEventsViewState(acceptedEvents = testAcceptedEventList, interestingEvents = testInterestingEventList))
    }

    @Test
    fun testSuccessfulEventFetchingWithoutProgressBar() {
        myEventsViewRobot.triggerEventsFetching(false)

        myEventsViewRobot.assertViewStates(
                MyEventsViewState(),
                MyEventsViewState(acceptedEvents = testAcceptedEventList, interestingEvents = testInterestingEventList))
    }

    @Test
    fun testSuccessfulEventJoining() {
        myEventsViewRobot.triggerEventsFetching(false)
        myEventsViewRobot.joinEvent(JoinEventData("2", "testHelloText"))
        val newPendingList = listOf(Event("2", "TestInterestingEvent", "testGameId2", type = EventType.PENDING))

        myEventsViewRobot.assertViewStates(
                MyEventsViewState(),
                MyEventsViewState(acceptedEvents = testAcceptedEventList, interestingEvents = testInterestingEventList),
                MyEventsViewState(acceptedEvents = testAcceptedEventList, interestingEvents = testInterestingEventList, joinRequestSent = true),
                MyEventsViewState(acceptedEvents = testAcceptedEventList, interestingEvents = testInterestingEventList, joinRequestSent = false),
                MyEventsViewState(acceptedEvents = testAcceptedEventList, pendingEvents = newPendingList))
    }
}