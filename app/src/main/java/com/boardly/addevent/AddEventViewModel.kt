package com.boardly.addevent

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class AddEventViewModel(private val addEventInteractor: AddEventInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(AddEventViewState())

    fun bind(addEventView: AddEventView) {
        val gamePickEventObservable = addEventView.emitGamePickEvent()
                .flatMap { addEventInteractor.fetchGameDetails(it).startWith(PartialAddEventViewState.GamePickedState()) }

        val placePickEventObservable = addEventView.emitPlacePickEvent()
                .map { PartialAddEventViewState.PlacePickedState() }

        val inputDataObservable = addEventView.emitInputData()
                .flatMap {
                    val eventNameValid = it.eventName.isNotBlank()
                    val numberOfPlayersValid = it.maxPlayers > 0
                    val selectedGameValid = it.gameId.isNotBlank()
                    val selectedPlaceValid = it.placeName.isNotBlank()

                    if (eventNameValid
                            && numberOfPlayersValid
                            && selectedGameValid
                            && selectedPlaceValid) {
                        addEventInteractor.addEvent(it)
                                .startWith(PartialAddEventViewState.ProgressState())
                    } else {
                        Observable.just(PartialAddEventViewState.LocalValidation(
                                eventNameValid,
                                numberOfPlayersValid,
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

        compositeDisposable.add(mergedObservable.subscribe { addEventView.render(it) })
    }

    private fun reduce(previousState: AddEventViewState, partialState: PartialAddEventViewState)
            : AddEventViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}