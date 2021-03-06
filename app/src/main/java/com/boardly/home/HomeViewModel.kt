package com.boardly.home

import android.arch.lifecycle.ViewModel
import com.boardly.analytics.Analytics
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class HomeViewModel(private val homeInteractor: HomeInteractor,
                    private val analytics: Analytics) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(HomeViewState())

    fun bind(homeView: HomeView) {
        val filteredFetchObservable = homeView.filteredFetchTriggerEmitter()
                .flatMap {
                    with(it) {
                        val eventsObservable = homeInteractor.fetchEvents(filter.userLocation, filter.radius, filter.gameId)
                        return@flatMap when (init) {
                            true -> eventsObservable.startWith(PartialHomeViewState.ProgressState())
                            false -> eventsObservable
                        }
                    }
                }

        val joinEventObservable = homeView.joinEventEmitter()
                .flatMap {
                    analytics.logJoinRequestSentEvent()
                    homeInteractor.joinEvent(it)
                }

        val updateEventListObservable = homeView.joinEventEmitter()
                .map { joinEventData ->
                    val currentState = stateSubject.value ?: HomeViewState()
                    val eventList = currentState.eventList.filter { joinEventData.eventId != it.eventId }
                    PartialHomeViewState.EventListState(eventList)
                }

        val locationProcessingObservable = homeView
                .locationProcessingEmitter()
                .filter { it }
                .map { PartialHomeViewState.LocationProcessingState() }

        val mergedObservable = Observable.merge(listOf(
                filteredFetchObservable,
                joinEventObservable,
                updateEventListObservable,
                locationProcessingObservable))
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