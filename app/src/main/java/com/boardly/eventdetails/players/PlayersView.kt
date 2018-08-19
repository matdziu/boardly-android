package com.boardly.eventdetails.players

import io.reactivex.Observable

interface PlayersView {

    fun render(playersViewState: PlayersViewState)

    fun fetchEventPlayersTriggerEmitter(): Observable<String>
}