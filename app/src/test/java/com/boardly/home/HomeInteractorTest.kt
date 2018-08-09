package com.boardly.home

import com.boardly.common.events.models.Event
import com.boardly.home.models.JoinEventData
import com.boardly.home.models.UserLocation
import com.boardly.home.network.HomeService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class HomeInteractorTest {

    private val testEventList = listOf(Event("1", "testEvent1"), Event("2", "testEvent2"))
    private val testEventIdsList = listOf("1")
    private val homeService: HomeService = mock {
        on { it.fetchAllEvents(any(), any(), any()) } doReturn Observable.just(testEventList)
        on { it.fetchUserEventIds() } doReturn Observable.just(testEventIdsList)
        on { it.sendJoinRequest(any()) } doReturn Observable.just(true)
    }
    private val homeInteractor = HomeInteractor(homeService)

    @Test
    fun testSuccessfulEventsFetching() {
        homeInteractor.fetchEvents(UserLocation(0.0, 0.0), 1.0, "testGameId").test()
                .assertValue(PartialHomeViewState.EventListState(listOf(Event("2", "testEvent2"))))
    }

    @Test
    fun testSuccessfulJoinEventRequest() {
        homeInteractor.joinEvent(JoinEventData("testEventId", "testHelloText")).test()
                .assertValue { it is PartialHomeViewState.JoinRequestSent }
    }
}