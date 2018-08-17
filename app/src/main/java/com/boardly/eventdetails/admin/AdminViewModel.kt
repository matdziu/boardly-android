package com.boardly.eventdetails.admin

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class AdminViewModel(private val adminInteractor: AdminInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(AdminViewState())

    fun bind(adminView: AdminView) {
        val fetchPendingPlayersObservable = adminView.fetchEventPlayersTriggerEmitter()
                .flatMap { adminInteractor.fetchPendingPlayers(it).startWith(PartialAdminViewState.PendingProgressState()) }

        val fetchAcceptedPlayersObservable = adminView.fetchEventPlayersTriggerEmitter()
                .flatMap { adminInteractor.fetchAcceptedPlayers(it).startWith(PartialAdminViewState.AcceptedProgressState()) }

        val mergedObservable = Observable.merge(fetchPendingPlayersObservable, fetchAcceptedPlayersObservable)
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