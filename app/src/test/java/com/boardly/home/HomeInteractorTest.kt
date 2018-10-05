package com.boardly.home

import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.common.location.UserLocation
import com.boardly.home.models.JoinEventData
import com.boardly.home.network.HomeService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class HomeInteractorTest {

    private val testEventList = listOf(
            Event(eventId = "1",
                    eventName = "testEvent1"),
            Event(eventId = "2",
                    eventName = "testEvent2"),
            Event(eventId = "3",
                    eventName = "testEvent3",
                    timestamp = 1000))
    private val testCreatedEventsList = listOf(
            Event(eventId = "4",
                    eventName = "testEvent4"))
    private val testEventIdsList = listOf("1")
    private val homeService: HomeService = mock {
        on { it.fetchAllEvents(any(), any(), any()) } doReturn Observable.just(testEventList)
        on { it.fetchUserEvents() } doReturn Observable.just(testEventIdsList)
        on { it.sendJoinRequest(any()) } doReturn Observable.just(true)
        on { it.fetchCreatedEvents() } doReturn Observable.just(testCreatedEventsList)
    }
    private val homeInteractor = HomeInteractor(homeService)

    @Test
    fun testSuccessfulEventsFetching() {
        homeInteractor.fetchEvents(UserLocation(0.0, 0.0), 1.0, "testGameId").test()
                .assertValue(PartialHomeViewState.EventListState(listOf(
                        Event("2", "testEvent2"),
                        Event("4", "testEvent4", type = EventType.CREATED))))
    }

    @Test
    fun testSuccessfulJoinEventRequest() {
        homeInteractor.joinEvent(JoinEventData("testEventId", "testHelloText")).test()
                .assertValues(PartialHomeViewState.JoinRequestSent(),
                        PartialHomeViewState.JoinRequestSent(false))
    }

    @Test
    fun testEventFetchingWhenUserLocationIsNull() {
        homeInteractor.fetchEvents(null, 0.0, "testGameId").test()
                .assertValue(PartialHomeViewState.EventListState(arrayListOf()))
    }
}