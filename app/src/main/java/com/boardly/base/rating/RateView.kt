package com.boardly.base.rating

import io.reactivex.Observable

interface RateView {

    fun ratingEmitter(): Observable<Int>

    fun emitRating(rating: Int)
}