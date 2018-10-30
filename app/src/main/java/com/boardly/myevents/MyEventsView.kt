package com.boardly.myevents

import com.boardly.base.joinevent.BaseJoinEventView
import io.reactivex.Observable

interface MyEventsView : BaseJoinEventView {

    fun render(myEventsViewState: MyEventsViewState)

    fun fetchEventsTriggerEmitter(): Observable<Boolean>
}