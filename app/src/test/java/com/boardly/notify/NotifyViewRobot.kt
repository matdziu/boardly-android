package com.boardly.notify

import com.boardly.base.BaseViewRobot
import com.boardly.notify.models.NotifySettings
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NotifyViewRobot(notifyViewModel: NotifyViewModel) : BaseViewRobot<NotifyViewState>() {

    private val gameIdSubject = PublishSubject.create<String>()
    private val notifySettingsSubject = PublishSubject.create<NotifySettings>()
    private val notifySettingsFetchSubject = PublishSubject.create<Boolean>()
    private val stopNotificationsButtonClickSubject = PublishSubject.create<Boolean>()
    private val placePickEventSubject = PublishSubject.create<Boolean>()

    private val notifyView = object : NotifyView {

        override fun render(notifyViewState: NotifyViewState) {
            renderedStates.add(notifyViewState)
        }

        override fun gameIdEmitter(): Observable<String> = gameIdSubject

        override fun notifySettingsEmitter(): Observable<NotifySettings> = notifySettingsSubject

        override fun notifySettingsFetchEmitter(): Observable<Boolean> = notifySettingsFetchSubject

        override fun stopNotificationsButtonClickEmitter(): Observable<Boolean> = stopNotificationsButtonClickSubject

        override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject
    }

    init {
        notifyViewModel.bind(notifyView)
    }

    fun emitGameId(gameId: String) {
        gameIdSubject.onNext(gameId)
    }

    fun emitNotifySettings(notifySettings: NotifySettings) {
        notifySettingsSubject.onNext(notifySettings)
    }

    fun triggerNotifySettingsFetch(init: Boolean) {
        notifySettingsFetchSubject.onNext(init)
    }

    fun deleteNotifySettings() {
        stopNotificationsButtonClickSubject.onNext(true)
    }

    fun emitPlacePickEvent() {
        placePickEventSubject.onNext(true)
    }
}