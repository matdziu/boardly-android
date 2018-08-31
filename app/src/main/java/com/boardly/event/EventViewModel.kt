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

        val addEventObservable = eventView.addEventEmitter()
                .flatMap {
                    validateInputData(it, { eventInteractor.addEvent(it) })
                }

        val editEventObservable = eventView.editEventEmitter()
                .flatMap { validateInputData(it, { eventInteractor.editEvent(it) }) }

        val removeEventObservable = eventView.deleteEventEmitter()
                .flatMap { eventInteractor.deleteEvent(it) }

        val mergedObservable = Observable.merge(listOf(
                gamePickEventObservable,
                placePickEventObservable,
                addEventObservable,
                editEventObservable,
                removeEventObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { eventView.render(it) })
    }

    private fun validateInputData(inputData: InputData, actionWhenValid: (InputData) -> Observable<PartialEventViewState>)
            : Observable<PartialEventViewState> {
        return with(inputData) {
            val eventNameValid = eventName.isNotBlank()
            val selectedGameValid = gameId.isNotBlank()
            val selectedPlaceValid = placeName.isNotBlank()

            if (eventNameValid
                    && selectedGameValid
                    && selectedPlaceValid) {
                actionWhenValid(this).startWith(PartialEventViewState.ProgressState())
            } else {
                Observable.just(PartialEventViewState.LocalValidation(
                        eventNameValid,
                        selectedGameValid,
                        selectedPlaceValid))
            }
        }
    }

    private fun reduce(previousState: EventViewState, partialState: PartialEventViewState)
            : EventViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}