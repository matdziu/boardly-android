package com.boardly.discover

import com.boardly.common.location.UserLocation
import com.boardly.discover.models.Place
import com.boardly.discover.network.DiscoverService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class DiscoverInteractorTest {

    @Test
    fun testSuccessfulPlacesListFetching() {
        val testPlaces = listOf(Place("1", "111", "Domowka"), Place("2", "222", "Hex"))
        val discoverService: DiscoverService = mock {
            on { it.fetchPlacesList(any(), any()) } doReturn Observable.just(testPlaces)
        }
        val discoverInteractor = DiscoverInteractor(discoverService)
        discoverInteractor.fetchPlacesList(UserLocation(10.0, 10.0), 10.0)
                .test()
                .assertValue(PartialDiscoverViewState.PlacesListFetched(testPlaces))
    }
}