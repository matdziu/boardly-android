package com.boardly.pickgame

import io.reactivex.Observable

@Deprecated("Can't use BGG API")
interface PickGameView {

    fun render(pickGameViewState: PickGameViewState)

    fun queryEmitter(): Observable<String>
}