package com.boardly.eventdetails.players

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class PlayersViewModel(private val playersInteractor: PlayersInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(PlayersViewState())

    fun bind(playersView: PlayersView) {
        val fetchEventPlayersObservable = playersView.fetchEventPlayersTriggerEmitter()
                .flatMap { playersInteractor.fetchAcceptedPlayers(it).startWith(PartialPlayersViewState.ProgressState()) }

        val mergedObservable = Observable.merge(listOf(fetchEventPlayersObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { playersView.render(it) })
    }

    private fun reduce(previousState: PlayersViewState, partialState: PartialPlayersViewState): PlayersViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}