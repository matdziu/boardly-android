package com.boardly.home

import com.boardly.common.events.models.Event
import com.boardly.filter.models.Filter
import com.boardly.home.models.UserLocation
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class HomeViewModelTest {

    private val testEventList = listOf(Event("1", "TestEvent", "testGameId"))
    private val homeInteractor: HomeInteractor = mock {
        on { it.fetchEvents(any(), any(), any()) } doReturn
                Observable.just(PartialHomeViewState.EventsFetchedState(testEventList))
                        .cast(PartialHomeViewState::class.java)
    }
    private val homeViewModel = HomeViewModel(homeInteractor)
    private val homeViewRobot = HomeViewRobot(homeViewModel)

    @Test
    fun testSuccessfulEventFetching() {
        homeViewRobot.emitFilteredFetchTrigger(UserLocation(1.0, 2.0), Filter())

        homeViewRobot.assertViewStates(
                HomeViewState(),
                HomeViewState(progress = true),
                HomeViewState(eventList = testEventList))
    }
}