package com.boardly.pickgame

import io.reactivex.Observable

interface PickGameView {

    fun render(pickGameViewState: PickGameViewState)

    fun queryEmitter(): Observable<String>
}