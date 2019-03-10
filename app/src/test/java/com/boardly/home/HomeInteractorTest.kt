package com.boardly.home

import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.common.location.DistanceCalculator
import com.boardly.common.location.UserLocation
import com.boardly.home.models.JoinEventData
import com.boardly.home.network.HomeService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class HomeInteractorTest {

    private val testEventList = listOf(
            Event(eventId = "1",
                    eventName = "testEvent1",
                    gameId = "testGameId"),
            Event(eventId = "2",
                    eventName = "testEvent2",
                    gameId = "testGameId"),
            Event(eventId = "3",
                    eventName = "testEvent3",
                    timestamp = 1000,
                    gameId = "testGameId"))
    private val testCreatedEventsList = listOf(
            Event(eventId = "4",
                    eventName = "testEvent4",
                    gameId = "testGameId"))
    private val testEventIdsList = listOf("1")
    private val homeService: HomeService = mock {
        on { it.fetchAllEvents(any(), any(), any()) } doReturn Observable.just(testEventList)
        on { it.fetchUserEvents() } doReturn Observable.just(testEventIdsList)
        on { it.sendJoinRequest(any()) } doReturn Observable.just(true)
        on { it.fetchCreatedEvents() } doReturn Observable.just(testCreatedEventsList)
    }
    private val distanceCalculator: DistanceCalculator = mock {
        on { it.distanceBetween(eq(0.0), eq(0.0), any(), any()) } doReturn 0.0f
        on { it.distanceBetween(eq(-1.0), eq(-1.0), any(), any()) } doReturn 10000.0f
    }
    private val homeInteractor = HomeInteractor(homeService, distanceCalculator)

    @Test
    fun testSuccessfulEventsFetchingInsideRadius() {
        homeInteractor.fetchEvents(UserLocation(0.0, 0.0), 1.0, "testGameId").test()
                .assertValue(PartialHomeViewState.EventListState(listOf(
                        Event("1", "testEvent1", type = EventType.CREATED, gameId = "testGameId"),
                        Event("2", "testEvent2", gameId = "testGameId"))))
    }

    @Test
    fun testSuccessfulEventsFetchingOutsideRadius() {
        homeInteractor.fetchEvents(UserLocation(-1.0, -1.0), 1.0, "testGameId").test()
                .assertValue(PartialHomeViewState.EventListState(listOf(
                        Event("2", "testEvent2", gameId = "testGameId"))))
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