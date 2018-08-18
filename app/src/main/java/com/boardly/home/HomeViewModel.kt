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
                .flatMap {
                    val userLocation = it.first
                    val filter = it.second
                    homeInteractor.fetchEvents(userLocation, filter.radius, filter.gameId)
                            .startWith(PartialHomeViewState.ProgressState())
                }

        val joinEventObservable = homeView.joinEventEmitter()
                .flatMap { homeInteractor.joinEvent(it) }

        val updateEventListObservable = homeView.joinEventEmitter()
                .map { joinEventData ->
                    val currentState = stateSubject.value ?: HomeViewState()
                    val eventList = currentState.eventList.filter { joinEventData.eventId != it.eventId }
                    PartialHomeViewState.EventListState(eventList)
                }

        val mergedObservable = Observable.merge(listOf(
                filteredFetchObservable,
                joinEventObservable,
                updateEventListObservable))
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