package com.boardly.myevents

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class MyEventsViewModel(private val myEventsInteractor: MyEventsInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(MyEventsViewState())

    fun bind(myEventsView: MyEventsView) {
        val eventsFetchObservable = myEventsView.fetchEventsTriggerEmitter()
                .flatMap {
                    val myEventsObservable = myEventsInteractor.fetchEvents()
                    return@flatMap when (it) {
                        true -> myEventsObservable.startWith(PartialMyEventsViewState.ProgressState())
                        false -> myEventsObservable
                    }
                }

        val mergedObservable = Observable.merge(listOf(eventsFetchObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { myEventsView.render(it) })
    }

    private fun reduce(previousState: MyEventsViewState, partialState: PartialMyEventsViewState)
            : MyEventsViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}