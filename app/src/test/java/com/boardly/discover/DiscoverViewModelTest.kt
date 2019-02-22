package com.boardly.discover

import com.boardly.common.location.UserLocation
import com.boardly.discover.models.FilteredFetchData
import com.boardly.discover.models.Place
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class DiscoverViewModelTest {

    private val discoverInteractor: DiscoverInteractor = mock()
    private val discoverViewModel = DiscoverViewModel(discoverInteractor)
    private val discoverViewRobot = DiscoverViewRobot(discoverViewModel)

    @Test
    fun testSuccessfulPlacesListFetching() {
        val testPlaces = listOf(Place("1", "111", "Domowka"), Place("2", "222", "Hex"))
        whenever(discoverInteractor.fetchPlacesList(any(), any())).doReturn(Observable.just(PartialDiscoverViewState.PlacesListFetched(testPlaces))
                .cast(PartialDiscoverViewState::class.java))
        discoverViewRobot.triggerPlacesFetch(FilteredFetchData(UserLocation(10.0, 10.0), 10.0))
        discoverViewRobot.assertViewStates(
                DiscoverViewState(),
                DiscoverViewState(
                        progress = false,
                        placesList = testPlaces)
        )
    }
}