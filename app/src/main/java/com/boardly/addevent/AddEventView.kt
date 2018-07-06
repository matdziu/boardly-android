package com.boardly.addevent

import io.reactivex.Observable

interface AddEventView {

    fun render(addEventViewState: AddEventViewState)

    fun emitInputData(): Observable<InputData>

    fun emitGamePickEvent(): Observable<String>

    fun emitPlacePickEvent(): Observable<Boolean>
}