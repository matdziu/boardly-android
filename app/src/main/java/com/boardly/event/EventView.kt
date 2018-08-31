package com.boardly.event

import io.reactivex.Observable

interface EventView {

    fun render(eventViewState: EventViewState)

    fun inputDataEmitter(): Observable<InputData>

    fun gamePickEventEmitter(): Observable<String>

    fun placePickEventEmitter(): Observable<Boolean>
}