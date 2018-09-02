package com.boardly.eventdetails.admin

import com.boardly.base.eventdetails.EventDetailsView
import io.reactivex.Observable

interface AdminView : EventDetailsView {

    fun render(adminViewState: AdminViewState)

    fun fetchEventPlayersTriggerEmitter(): Observable<Boolean>

    fun kickPlayerEmitter(): Observable<String>

    fun acceptPlayerEmitter(): Observable<String>
}