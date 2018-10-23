package com.boardly.notify.network

import com.boardly.notify.models.NotifySettings
import io.reactivex.Observable

interface NotifyService {

    fun saveNotifySettings(notifySettings: NotifySettings): Observable<Boolean>
}