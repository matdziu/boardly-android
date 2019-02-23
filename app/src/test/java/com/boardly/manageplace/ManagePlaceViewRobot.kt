package com.boardly.manageplace

import com.boardly.base.BaseViewRobot
import com.boardly.discover.models.Place
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ManagePlaceViewRobot(managePlaceViewModel: ManagePlaceViewModel) : BaseViewRobot<ManagePlaceViewState>() {

    private val placeDataEmitterSubject = PublishSubject.create<Place>()
    private val fetchPlaceDataTriggerSubject = PublishSubject.create<Boolean>()

    private val managePlaceView = object : ManagePlaceView {

        override fun render(managePlaceViewState: ManagePlaceViewState) {
            renderedStates.add(managePlaceViewState)
        }

        override fun placeDataEmitter(): Observable<Place> {
            return placeDataEmitterSubject
        }

        override fun fetchPlaceDataTriggerEmitter(): Observable<Boolean> {
            return fetchPlaceDataTriggerSubject
        }
    }

    init {
        managePlaceViewModel.bind(managePlaceView)
    }

    fun emitPlace(place: Place) {
        placeDataEmitterSubject.onNext(place)
    }

    fun emitFetchTrigger() {
        fetchPlaceDataTriggerSubject.onNext(true)
    }
}