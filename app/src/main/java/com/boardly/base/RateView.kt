package com.boardly.base

import io.reactivex.Observable

interface RateView {

    fun ratingEmitter(): Observable<Int>

    fun emitRating(rating: Int)
}