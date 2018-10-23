package com.boardly.notify.network

import com.boardly.notify.models.NotifySettings
import io.reactivex.Observable

interface NotifyService {

    fun updateNotifySettings(notifySettings: NotifySettings): Observable<Boolean>

    fun fetchNotifySettings(): Observable<NotifySettings>
}