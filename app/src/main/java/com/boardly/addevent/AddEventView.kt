package com.boardly.addevent

import io.reactivex.Observable

interface AddEventView {

    fun render(addEventViewState: AddEventViewState)

    fun emitPickedGameId(): Observable<String>
}