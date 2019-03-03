package com.boardly.notify

import android.arch.lifecycle.ViewModel
import com.boardly.notify.models.NotifySettings
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class NotifyViewModel(private val notifyInteractor: NotifyInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(NotifyViewState())

    fun bind(notifyView: NotifyView) {
        val notifySettingsObservable = notifyView.notifySettingsEmitter()
                .flatMap { validateNotifySettings(it, { notifyInteractor.updateNotifySettings(it) }) }

        val notifySettingsFetchObservable = notifyView.notifySettingsFetchEmitter()
                .filter { it }
                .flatMap { notifyInteractor.fetchNotifySettings().startWith(PartialNotifyViewState.ProgressState()) }

        val stopNotificationsObservable = notifyView.stopNotificationsButtonClickEmitter()
                .flatMap { notifyInteractor.deleteNotifications().startWith(PartialNotifyViewState.ProgressState()) }

        val placePickEventObservable = notifyView.placePickEventEmitter()
                .map { PartialNotifyViewState.PlacePickedState() }

        val mergedObservable = Observable.merge(listOf(
                notifySettingsObservable,
                notifySettingsFetchObservable,
                stopNotificationsObservable,
                placePickEventObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { notifyView.render(it) })
    }

    private fun validateNotifySettings(notifySettings: NotifySettings,
                                       actionWhenValid: (NotifySettings) -> Observable<PartialNotifyViewState>)
            : Observable<PartialNotifyViewState> {
        return with(notifySettings) {
            val selectedPlaceValid = locationName.isNotBlank()
            if (selectedPlaceValid) actionWhenValid(this).startWith(PartialNotifyViewState.ProgressState())
            else Observable.just(PartialNotifyViewState.LocalValidation(selectedPlaceValid))
        }
    }

    private fun reduce(previousState: NotifyViewState, partialState: PartialNotifyViewState):
            NotifyViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}