package com.boardly.eventdetails.players

import com.boardly.base.RateView
import io.reactivex.Observable

interface PlayersView : RateView {

    fun render(playersViewState: PlayersViewState)

    fun fetchEventPlayersTriggerEmitter(): Observable<String>
}