package com.boardly.pickplace

import io.reactivex.Observable

interface PickPlaceView {

    fun render(pickPlaceViewState: PickPlaceViewState)

    fun queryEmitter(): Observable<String>
}