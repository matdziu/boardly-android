package com.boardly.filter

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class FilterViewModel(private val filterInteractor: FilterInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(FilterViewState())

    fun bind(filterView: FilterView) {
        val locationProcessingObservable = filterView.locationProcessingEmitter()
                .map { PartialFilterViewState.LocationProcessingState(it) }

        val mergedObservable = Observable.merge(listOf(locationProcessingObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { filterView.render(it) })
    }

    private fun reduce(previousState: FilterViewState, partialState: PartialFilterViewState):
            FilterViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}