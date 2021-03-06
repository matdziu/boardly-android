package com.boardly.eventdetails.admin

import android.arch.lifecycle.ViewModel
import com.boardly.analytics.Analytics
import com.boardly.extensions.mapToRatedPlayerCopy
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class AdminViewModel(private val adminInteractor: AdminInteractor,
                     private val analytics: Analytics,
                     initialState: AdminViewState = AdminViewState()) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(initialState)

    fun bind(adminView: AdminView, eventId: String) {
        val fetchEventTriggerObservable = adminView.fetchEventDetailsTriggerEmitter()
                .filter { it }
                .flatMap { adminInteractor.fetchEvent(eventId).startWith(PartialAdminViewState.EventProgressState()) }

        val fetchPendingPlayersObservable = adminView.fetchEventDetailsTriggerEmitter()
                .filter { it }
                .flatMap { adminInteractor.fetchPendingPlayers(eventId).startWith(PartialAdminViewState.PendingProgressState()) }

        val fetchAcceptedPlayersObservable = adminView.fetchEventDetailsTriggerEmitter()
                .filter { it }
                .flatMap { adminInteractor.fetchAcceptedPlayers(eventId).startWith(PartialAdminViewState.AcceptedProgressState()) }

        val acceptPlayerObservable = adminView.acceptPlayerEmitter()
                .flatMap {
                    analytics.logJoinRequestAcceptedEvent()
                    adminInteractor.acceptPlayer(eventId, it)
                }

        val updatePendingListObservable = adminView.acceptPlayerEmitter()
                .map { playerId ->
                    val currentState = stateSubject.value ?: AdminViewState()
                    val pendingList = currentState.pendingPlayersList.filter { playerId != it.id }
                    PartialAdminViewState.PendingListState(pendingList)
                }

        val kickPlayerObservable = adminView.kickPlayerEmitter()
                .flatMap { adminInteractor.kickPlayer(eventId, it) }

        val updateAcceptedListObservable = adminView.kickPlayerEmitter()
                .map { playerId ->
                    val currentState = stateSubject.value ?: AdminViewState()
                    val acceptedList = currentState.acceptedPlayersList.filter { playerId != it.id }
                    PartialAdminViewState.AcceptedListState(acceptedList)
                }

        val sendRatingObservable = adminView.ratingEmitter()
                .flatMap { adminInteractor.sendRating(it) }

        val updateRatedOrSelfObservable = adminView.ratingEmitter()
                .map { rateInput ->
                    val currentState = stateSubject.value ?: AdminViewState()
                    val acceptedList = currentState.acceptedPlayersList
                            .mapToRatedPlayerCopy { it.id == rateInput.playerId }
                    PartialAdminViewState.AcceptedListState(acceptedList)
                }

        val mergedObservable = Observable.merge(listOf(
                fetchEventTriggerObservable,
                fetchPendingPlayersObservable,
                fetchAcceptedPlayersObservable,
                acceptPlayerObservable,
                kickPlayerObservable,
                updatePendingListObservable,
                updateAcceptedListObservable,
                sendRatingObservable,
                updateRatedOrSelfObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { adminView.render(it) })
    }

    private fun reduce(previousState: AdminViewState, partialState: PartialAdminViewState): AdminViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}