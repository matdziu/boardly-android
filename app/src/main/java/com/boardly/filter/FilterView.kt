package com.boardly.filter

import io.reactivex.Observable

interface FilterView {

    fun render(filterViewState: FilterViewState)

    fun gameIdEmitter(): Observable<String>
}