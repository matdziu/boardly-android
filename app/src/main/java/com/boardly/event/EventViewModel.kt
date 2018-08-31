package com.boardly.event

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class EventViewModel(private val eventInteractor: EventInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(EventViewState())

    fun bind(eventView: EventView) {
        val gamePickEventObservable = eventView.gamePickEventEmitter()
                .flatMap { eventInteractor.fetchGameDetails(it).startWith(PartialEventViewState.GamePickedState()) }

        val placePickEventObservable = eventView.placePickEventEmitter()
                .map { PartialEventViewState.PlacePickedState() }

        val inputDataObservable = eventView.inputDataEmitter()
                .flatMap {
                    val eventNameValid = it.eventName.isNotBlank()
                    val selectedGameValid = it.gameId.isNotBlank()
                    val selectedPlaceValid = it.placeName.isNotBlank()

                    if (eventNameValid
                            && selectedGameValid
                            && selectedPlaceValid) {
                        eventInteractor.addEvent(it)
                                .startWith(PartialEventViewState.ProgressState())
                    } else {
                        Observable.just(PartialEventViewState.LocalValidation(
                                eventNameValid,
                                selectedGameValid,
                                selectedPlaceValid))
                    }
                }

        val mergedObservable = Observable.merge(listOf(
                gamePickEventObservable,
                placePickEventObservable,
                inputDataObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { eventView.render(it) })
    }

    private fun reduce(previousState: EventViewState, partialState: PartialEventViewState)
            : EventViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}