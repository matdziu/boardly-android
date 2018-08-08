package com.boardly.myevents

import com.boardly.common.events.models.Event
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class MyEventsViewModelTest {

    private val testEventList = listOf(Event("1", "TestEvent", "testGameId"))
    private val myEventsInteractor: MyEventsInteractor = mock {
        on { it.fetchEvents() } doReturn Observable.just(PartialMyEventsViewState.EventsFetchedState(testEventList))
                .cast(PartialMyEventsViewState::class.java)
    }
    private val myEventsViewModel = MyEventsViewModel(myEventsInteractor)
    private val myEventsViewRobot = MyEventsViewRobot(myEventsViewModel)

    @Test
    fun testSuccessfulEventFetching() {
        myEventsViewRobot.triggerEventsFetching()

        myEventsViewRobot.assertViewStates(
                MyEventsViewState(),
                MyEventsViewState(progress = true),
                MyEventsViewState(eventsList = testEventList))
    }
}