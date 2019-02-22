package com.boardly.discover

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class DiscoverViewModel(private val discoverInteractor: DiscoverInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(DiscoverViewState())

    fun bind(discoverView: DiscoverView) {

        val placesListFetchObservable = discoverView.fetchPlacesListTrigger()
                .flatMap { discoverInteractor.fetchPlacesList(it.userLocation, it.radius) }

        val mergedObservable = Observable.merge(
                listOf(placesListFetchObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { discoverView.render(it) })
    }

    private fun reduce(previousState: DiscoverViewState, partialState: PartialDiscoverViewState)
            : DiscoverViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}