package com.boardly.addevent

import io.reactivex.Observable

interface AddEventView {

    fun render(addEventViewState: AddEventViewState)

    fun inputDataEmitter(): Observable<InputData>

    fun gamePickEventEmitter(): Observable<String>

    fun placePickEventEmitter(): Observable<Boolean>
}