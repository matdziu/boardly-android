package com.boardly.home

import com.boardly.common.events.models.Event
import com.boardly.common.location.UserLocation
import com.boardly.filter.models.Filter
import com.boardly.home.models.FilteredFetchData
import com.boardly.home.models.JoinEventData
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class HomeViewModelTest {

    private val testEventList = listOf(Event("1", "TestEvent", "testGameId"))
    private val homeInteractor: HomeInteractor = mock {
        on { it.fetchEvents(any(), any(), any()) } doReturn
                Observable.just(PartialHomeViewState.EventListState(testEventList))
                        .cast(PartialHomeViewState::class.java)
        on { it.joinEvent(any()) } doReturn
                Observable.just(PartialHomeViewState.JoinRequestSent())
                        .cast(PartialHomeViewState::class.java)
    }
    private val homeViewModel = HomeViewModel(homeInteractor, mock())
    private val homeViewRobot = HomeViewRobot(homeViewModel)

    @Test
    fun testSuccessfulEventFetchingWithProgressBar() {
        homeViewRobot.emitFilteredFetchTrigger(FilteredFetchData(Filter(userLocation = UserLocation(1.0, 2.0)), true))

        homeViewRobot.assertViewStates(
                HomeViewState(),
                HomeViewState(progress = true),
                HomeViewState(eventList = testEventList))
    }

    @Test
    fun testSuccessfulEventFetchingWithoutProgressBar() {
        homeViewRobot.emitFilteredFetchTrigger(FilteredFetchData(Filter(userLocation = UserLocation(1.0, 2.0)), false))

        homeViewRobot.assertViewStates(
                HomeViewState(),
                HomeViewState(eventList = testEventList))
    }

    @Test
    fun testSuccessfulEventJoining() {
        homeViewRobot.emitFilteredFetchTrigger(FilteredFetchData(Filter(userLocation = UserLocation(1.0, 2.0)), true))
        homeViewRobot.joinEvent(JoinEventData("1", "This is test hello message"))

        homeViewRobot.assertViewStates(
                HomeViewState(),
                HomeViewState(progress = true),
                HomeViewState(eventList = testEventList),
                HomeViewState(eventList = testEventList, joinRequestSent = true),
                HomeViewState(joinRequestSent = false))
    }

    @Test
    fun whenLocationIsProcessedViewStateIndicatesIt() {
        homeViewRobot.processLocation(true)
        homeViewRobot.processLocation(false)
        homeViewRobot.assertViewStates(
                HomeViewState(),
                HomeViewState(locationProcessing = true))
    }
}