package com.boardly.event

import com.boardly.event.models.GamePickEvent
import io.reactivex.Observable

interface EventView {

    fun render(eventViewState: EventViewState)

    fun addEventEmitter(): Observable<InputData>

    fun editEventEmitter(): Observable<InputData>

    fun deleteEventEmitter(): Observable<String>

    fun gamePickEventEmitter(): Observable<GamePickEvent>

    fun placePickEventEmitter(): Observable<Boolean>
}