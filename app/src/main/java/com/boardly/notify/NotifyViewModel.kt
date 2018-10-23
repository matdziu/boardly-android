package com.boardly.notify

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class NotifyViewModel(private val notifyInteractor: NotifyInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(NotifyViewState())

    fun bind(notifyView: NotifyView) {
        val gameDetailsObservable = notifyView.gameIdEmitter()
                .flatMap { notifyInteractor.fetchGameDetails(it) }

        val notifySettingsObservable = notifyView.notifySettingsEmitter()
                .flatMap { notifyInteractor.saveNotifySettings(it).startWith(PartialNotifyViewState.ProgressState()) }

        val mergedObservable = Observable.merge(notifySettingsObservable, gameDetailsObservable)
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { notifyView.render(it) })
    }

    private fun reduce(previousState: NotifyViewState, partialState: PartialNotifyViewState):
            NotifyViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}