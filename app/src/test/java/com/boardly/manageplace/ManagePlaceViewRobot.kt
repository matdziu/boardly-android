package com.boardly.manageplace

import com.boardly.base.BaseViewRobot
import com.boardly.discover.models.Place
import com.boardly.manageplace.models.PlaceInputData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ManagePlaceViewRobot(managePlaceViewModel: ManagePlaceViewModel) : BaseViewRobot<ManagePlaceViewState>() {

    private val placeDataEmitterSubject = PublishSubject.create<PlaceInputData>()
    private val fetchPlaceDataTriggerSubject = PublishSubject.create<Boolean>()
    private val placePickEventSubject = PublishSubject.create<Boolean>()

    private val managePlaceView = object : ManagePlaceView {
        override fun placePickEventEmitter(): Observable<Boolean> {
            return placePickEventSubject
        }

        override fun render(managePlaceViewState: ManagePlaceViewState) {
            renderedStates.add(managePlaceViewState)
        }

        override fun placeDataEmitter(): Observable<PlaceInputData> {
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
        placeDataEmitterSubject.onNext(PlaceInputData(place))
    }

    fun emitFetchTrigger() {
        fetchPlaceDataTriggerSubject.onNext(true)
    }

    fun emitPlacePickEvent() {
        placePickEventSubject.onNext(true)
    }
}