package com.boardly.eventdetails.admin

import io.reactivex.Observable

interface AdminView {

    fun render(adminViewState: AdminViewState)

    fun fetchEventPlayersTriggerEmitter(): Observable<Boolean>

    fun kickPlayerEmitter(): Observable<String>

    fun acceptPlayerEmitter(): Observable<String>
}