package com.boardly.home

import android.arch.lifecycle.ViewModel
import com.boardly.common.events.models.Event
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class HomeViewModel(private val homeInteractor: HomeInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(HomeViewState())
    private var eventList = listOf<Event>()

    fun bind(homeView: HomeView) {
        val filteredFetchObservable = homeView.filteredFetchTriggerEmitter()
                .flatMap {
                    val userLocation = it.first
                    val filter = it.second
                    homeInteractor.fetchEvents(userLocation, filter.radius, filter.gameId)
                            .doOnNext { saveEventList(it) }
                            .startWith(PartialHomeViewState.ProgressState())
                }

        val joinEventObservable = homeView.joinEventEmitter()
                .flatMap { homeInteractor.joinEvent(it) }

        val updateEventListObservable = homeView.joinEventEmitter()
                .map { joinEventData ->
                    eventList = eventList.filter { joinEventData.eventId != it.eventId }
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

    private fun saveEventList(partialState: PartialHomeViewState) {
        val eventListState = partialState as PartialHomeViewState.EventListState
        eventList = eventListState.eventsList
    }

    private fun reduce(previousState: HomeViewState, partialState: PartialHomeViewState): HomeViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}