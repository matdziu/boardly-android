package com.boardly.manageplace

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class ManagePlaceViewModel(private val managePlaceInteractor: ManagePlaceInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(ManagePlaceViewState())

    fun bind(managePlaceView: ManagePlaceView) {
        val fetchPlaceDataObservable = managePlaceView.fetchPlaceDataTriggerEmitter()
                .flatMap { managePlaceInteractor.fetchPlaceData().startWith(PartialManagePlaceViewState.ProgressState) }

        val placeDataObservable = managePlaceView.placeDataEmitter()
                .flatMap {
                    val placeNameValid = it.place.name.isNotBlank()
                    val placeDescriptionValid = it.place.description.isNotBlank()
                    val placeLocationValid = it.place.locationName.isNotBlank()
                    val placeNumberValid = it.place.phoneNumber.isNotBlank()

                    if (placeNameValid && placeDescriptionValid
                            && placeLocationValid && placeNumberValid) {
                        managePlaceInteractor.savePlaceData(it).startWith(PartialManagePlaceViewState.ProgressState)
                    } else {
                        Observable.just(PartialManagePlaceViewState.LocalValidation(
                                placeNameValid,
                                placeDescriptionValid,
                                placeLocationValid,
                                placeNumberValid))
                    }
                }

        val placePickEventObservable = managePlaceView.placePickEventEmitter()
                .map { PartialManagePlaceViewState.PlacePickedState }

        val mergedObservable = Observable.merge(
                listOf(fetchPlaceDataObservable,
                        placeDataObservable,
                        placePickEventObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { managePlaceView.render(it) })
    }

    private fun reduce(previousState: ManagePlaceViewState, partialState: PartialManagePlaceViewState)
            : ManagePlaceViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}