package com.boardly.discover

import com.boardly.common.location.UserLocation
import com.boardly.discover.network.DiscoverService
import io.reactivex.Observable
import javax.inject.Inject

class DiscoverInteractor @Inject constructor(private val discoverService: DiscoverService) {

    fun fetchPlacesList(userLocation: UserLocation, radius: Double): Observable<PartialDiscoverViewState> {
        return discoverService.fetchPlacesList(userLocation, radius)
                .map { PartialDiscoverViewState.PlacesListFetched(it) }
    }
}