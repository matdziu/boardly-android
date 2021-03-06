package com.boardly.myevents

import com.boardly.common.events.models.Event
import com.boardly.myevents.network.MyEventsService
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class MyEventsInteractorTest {

    @Test
    fun testSuccessfulPendingAcceptedCreatedAndInterestingEventsMerging() {
        val acceptedEventsList = listOf(
                Event(eventId = "1",
                        eventName = "sampleAcceptedEvent"))
        val pendingEventsList = listOf(
                Event(eventId = "2",
                        eventName = "samplePendingEvent"))
        val createdEventsList = listOf(
                Event(eventId = "3",
                        eventName = "sampleCreatedEvent",
                        timestamp = 1000))
        val interestingEventsList = listOf(
                Event(eventId = "2",
                        eventName = "sampleInterestingEvent"))
        val myEventsService: MyEventsService = mock {
            on { it.getAcceptedEvents() } doReturn Observable.just(acceptedEventsList)
            on { it.getPendingEvents() } doReturn Observable.just(pendingEventsList)
            on { it.getCreatedEvents() } doReturn Observable.just(createdEventsList)
            on { it.getInterestingEvents() } doReturn Observable.just(interestingEventsList)
        }
        val myEventsInteractor = MyEventsInteractor(myEventsService)

        myEventsInteractor.fetchEvents().test()
                .assertValue(PartialMyEventsViewState.EventsFetchedState(acceptedEventsList, pendingEventsList, listOf(), listOf()))
    }
}