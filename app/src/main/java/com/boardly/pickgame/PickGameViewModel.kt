package com.boardly.pickgame

import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

@Deprecated("Can't use BGG API")
class PickGameViewModel(private val pickGameInteractor: PickGameInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(PickGameViewState())

    fun bind(pickGameView: PickGameView) {
        pickGameView.queryEmitter()
                .filter { it.length > 1 }
                .flatMap { pickGameInteractor.fetchSearchResults(it).startWith(PartialPickGameViewState.ProgressState()) }
                .scan(stateSubject.value, BiFunction(this::reduce))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateSubject)

        compositeDisposable.add(stateSubject.subscribe { pickGameView.render(it) })
    }

    private fun reduce(previousState: PickGameViewState, partialState: PartialPickGameViewState): PickGameViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}