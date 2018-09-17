package com.boardly.myevents

import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class MyEventsViewModelTest {

    private val testEventList = listOf(Event("1", "TestEvent", "testGameId", type = EventType.ACCEPTED))
    private val myEventsInteractor: MyEventsInteractor = mock {
        on { it.fetchEvents() } doReturn Observable.just(PartialMyEventsViewState.EventsFetchedState(testEventList, listOf(), listOf()))
                .cast(PartialMyEventsViewState::class.java)
    }
    private val myEventsViewModel = MyEventsViewModel(myEventsInteractor)
    private val myEventsViewRobot = MyEventsViewRobot(myEventsViewModel)

    @Test
    fun testSuccessfulEventFetchingWithProgressBar() {
        myEventsViewRobot.triggerEventsFetching(true)

        myEventsViewRobot.assertViewStates(
                MyEventsViewState(),
                MyEventsViewState(progress = true),
                MyEventsViewState(acceptedEvents = testEventList))
    }

    @Test
    fun testSuccessfulEventFetchingWithoutProgressBar() {
        myEventsViewRobot.triggerEventsFetching(false)

        myEventsViewRobot.assertViewStates(
                MyEventsViewState(),
                MyEventsViewState(acceptedEvents = testEventList))
    }
}