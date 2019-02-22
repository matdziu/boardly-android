package com.boardly.discover.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.location.UserLocation
import com.boardly.discover.models.Place
import io.reactivex.Observable

class DiscoverServiceImpl : DiscoverService, BaseServiceImpl() {

    override fun fetchPlacesList(userLocation: UserLocation, radius: Double): Observable<List<Place>> {
        return Observable.just(listOf(Place(), Place(), Place(), Place(), Place(), Place(), Place()))
    }
}