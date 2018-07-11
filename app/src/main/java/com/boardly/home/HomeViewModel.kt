package com.boardly.home

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class HomeViewModel(private val homeInteractor: HomeInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(HomeViewState())

    fun bind(homeView: HomeView) {
        val filteredFetchObservable = homeView.filteredFetchTriggerEmitter()
                .flatMap { homeInteractor.fetchEvents(it.radius, it.gameId).startWith(PartialHomeViewState.ProgressState()) }

        val mergedObservable = Observable.merge(listOf(filteredFetchObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { homeView.render(it) })
    }

    private fun reduce(previousState: HomeViewState, partialState: PartialHomeViewState): HomeViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}