package com.boardly.filter

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class FilterViewModel @Inject constructor(private val filterInteractor: FilterInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(FilterViewState())

    fun bind(filterView: FilterView) {
        filterView.gameIdEmitter()
                .flatMap { filterInteractor.fetchGameDetails(it) }
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribe(stateSubject)

        compositeDisposable.add(stateSubject.subscribe { filterView.render(it) })
    }

    private fun reduce(previousState: FilterViewState, partialState: PartialFilterViewState):
            FilterViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}