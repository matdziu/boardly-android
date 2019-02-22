package com.boardly.discover.network

import com.boardly.common.location.UserLocation
import com.boardly.discover.models.Place
import io.reactivex.Observable

interface DiscoverService {

    fun fetchPlacesList(userLocation: UserLocation, radius: Double): Observable<List<Place>>
}