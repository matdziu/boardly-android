package com.boardly.manageplace.network

import com.boardly.discover.models.Place
import com.boardly.manageplace.models.PlaceInputData
import io.reactivex.Observable

interface ManagePlaceService {

    fun fetchPlaceData(placeId: String): Observable<Place>

    fun savePlaceData(placeId: String, placeInputData: PlaceInputData): Observable<Boolean>

    fun fetchManagedPlaceId(): Observable<String>
}