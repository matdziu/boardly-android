package com.boardly.manageplace.network

import com.boardly.discover.models.Place
import io.reactivex.Observable

interface ManagePlaceService {

    fun fetchPlaceData(placeId: String): Observable<Place>

    fun savePlaceData(placeId: String, place: Place): Observable<Boolean>

    fun fetchManagedPlaceId(): Observable<String>
}