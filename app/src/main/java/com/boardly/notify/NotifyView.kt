package com.boardly.notify

import com.boardly.notify.models.NotifySettings
import io.reactivex.Observable

interface NotifyView {

    fun render(notifyViewState: NotifyViewState)

    fun gameIdEmitter(): Observable<String>

    fun notifySettingsEmitter(): Observable<NotifySettings>

    fun notifySettingsFetchEmitter(): Observable<Boolean>

    fun stopNotificationsButtonClickEmitter(): Observable<Boolean>

    fun placePickEventEmitter(): Observable<Boolean>
}