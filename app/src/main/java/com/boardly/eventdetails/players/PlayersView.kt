package com.boardly.eventdetails.players

import com.boardly.base.eventdetails.EventDetailsView
import io.reactivex.Observable

interface PlayersView : EventDetailsView {

    fun render(playersViewState: PlayersViewState)

    fun fetchEventDetailsTriggerEmitter(): Observable<Boolean>
}