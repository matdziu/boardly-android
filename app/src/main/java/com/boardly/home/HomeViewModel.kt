package com.boardly.home

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class HomeViewModel(private val homeInteractor: HomeInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(HomeViewState())

    fun bind(homeView: HomeView) {

    }

    private fun reduce(previousState: HomeViewState, partialState: PartialHomeViewState): HomeViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}