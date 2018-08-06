package com.boardly.myevents

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MyEventsViewModel(private val myEventsInteractor: MyEventsInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(MyEventsViewState())

    fun bind(myEventsView: MyEventsView) {

    }

    private fun reduce(previousState: MyEventsViewState, partialState: PartialMyEventsViewState)
            : MyEventsViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}