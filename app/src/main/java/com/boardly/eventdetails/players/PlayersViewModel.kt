package com.boardly.eventdetails.players

import android.arch.lifecycle.ViewModel
import com.boardly.extensions.mapToRatedPlayerCopy
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class PlayersViewModel(private val playersInteractor: PlayersInteractor,
                       initialState: PlayersViewState = PlayersViewState()) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(initialState)

    fun bind(playersView: PlayersView) {
        val fetchEventPlayersObservable = playersView.fetchEventPlayersTriggerEmitter()
                .flatMap { playersInteractor.fetchAcceptedPlayers(it).startWith(PartialPlayersViewState.ProgressState()) }

        val sendRatingObservable = playersView.ratingEmitter()
                .flatMap { playersInteractor.sendRating(it) }

        val updateRatedOrSelfObservable = playersView.ratingEmitter()
                .map { rateInput ->
                    val currentState = stateSubject.value ?: PlayersViewState()
                    val acceptedList = currentState.acceptedPlayersList
                            .mapToRatedPlayerCopy { it.id == rateInput.playerId }
                    PartialPlayersViewState.AcceptedListState(acceptedList)
                }

        val mergedObservable = Observable.merge(listOf(
                fetchEventPlayersObservable,
                sendRatingObservable,
                updateRatedOrSelfObservable))
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