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
        val pickedGameIdObservable = addEventView.emitPickedGameId()
                .flatMap { addEventInteractor.fetchGameDetails(it) }

        val mergedObservable = Observable.merge(listOf(pickedGameIdObservable))
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