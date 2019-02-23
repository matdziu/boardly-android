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
                .flatMap { managePlaceInteractor.fetchPlaceData() }

        val placeDataObservable = managePlaceView.placeDataEmitter()
                .flatMap {
                    val placeNameValid = it.name.isNotBlank()
                    val placeDescriptionValid = it.description.isNotBlank()
                    val placeLocationValid = it.locationName.isNotBlank()
                    val placeNumberValid = it.phoneNumber.isNotBlank()

                    if (placeNameValid && placeDescriptionValid
                            && placeLocationValid && placeNumberValid) {
                        managePlaceInteractor.savePlaceData(it)
                    } else {
                        Observable.just(PartialManagePlaceViewState.LocalValidation(
                                placeNameValid,
                                placeDescriptionValid,
                                placeLocationValid,
                                placeNumberValid))
                    }
                }

        val mergedObservable = Observable.merge(
                listOf(fetchPlaceDataObservable,
                        placeDataObservable))
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