package com.boardly.manageplace

import com.boardly.manageplace.models.PlaceInputData
import io.reactivex.Observable

interface ManagePlaceView {

    fun render(managePlaceViewState: ManagePlaceViewState)

    fun placeDataEmitter(): Observable<PlaceInputData>

    fun fetchPlaceDataTriggerEmitter(): Observable<Boolean>

    fun placePickEventEmitter(): Observable<Boolean>
}