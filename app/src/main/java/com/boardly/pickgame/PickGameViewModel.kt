package com.boardly.pickgame

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class PickGameViewModel(private val pickGameInteractor: PickGameInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(PickGameViewState())

    fun bind(pickGameView: PickGameView) {

    }

    fun unbind() {
        compositeDisposable.clear()
    }
}