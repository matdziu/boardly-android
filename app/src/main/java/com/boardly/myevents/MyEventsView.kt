package com.boardly.myevents

import io.reactivex.Observable

interface MyEventsView {

    fun render(myEventsViewState: MyEventsViewState)

    fun fetchEventsTriggerEmitter(): Observable<Boolean>
}