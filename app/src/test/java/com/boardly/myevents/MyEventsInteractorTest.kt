package com.boardly.myevents

import com.boardly.common.events.models.Event
import com.boardly.myevents.network.MyEventsService
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class MyEventsInteractorTest {

    @Test
    fun testSuccessfulPendingAcceptedAndCreatedEventsMerging() {
        val acceptedEventsList = listOf(Event(eventId = "1", eventName = "sampleAcceptedEvent"))
        val pendingEventsList = listOf(Event(eventId = "2", eventName = "samplePendingEvent"))
        val createdEventsList = listOf(Event(eventId = "3", eventName = "sampleCreatedEvent"))
        val myEventsService: MyEventsService = mock {
            on { it.getAcceptedEvents() } doReturn Observable.just(acceptedEventsList)
            on { it.getPendingEvents() } doReturn Observable.just(pendingEventsList)
            on { it.getCreatedEvents() } doReturn Observable.just(createdEventsList)
        }
        val myEventsInteractor = MyEventsInteractor(myEventsService)

        myEventsInteractor.fetchEvents().test()
                .assertValue(PartialMyEventsViewState.EventsFetchedState(acceptedEventsList + createdEventsList + pendingEventsList))
    }
}