package com.boardly.event

import io.reactivex.Observable

interface EventView {

    fun render(eventViewState: EventViewState)

    fun addEventEmitter(): Observable<InputData>

    fun editEventEmitter(): Observable<InputData>

    fun deleteEventEmitter(): Observable<String>

    fun gamePickEventEmitter(): Observable<String>

    fun placePickEventEmitter(): Observable<Boolean>
}