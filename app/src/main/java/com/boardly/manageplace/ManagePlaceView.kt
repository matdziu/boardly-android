package com.boardly.manageplace

import com.boardly.discover.models.Place
import io.reactivex.Observable

interface ManagePlaceView {

    fun render(managePlaceViewState: ManagePlaceViewState)

    fun placeDataEmitter(): Observable<Place>

    fun fetchPlaceDataTriggerEmitter(): Observable<Boolean>
}