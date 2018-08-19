package com.boardly.eventdetails.admin

import com.boardly.base.RateView
import io.reactivex.Observable

interface AdminView : RateView {

    fun render(adminViewState: AdminViewState)

    fun fetchEventPlayersTriggerEmitter(): Observable<Boolean>

    fun kickPlayerEmitter(): Observable<String>

    fun acceptPlayerEmitter(): Observable<String>
}