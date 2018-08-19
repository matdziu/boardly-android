package com.boardly.base.rating

import com.boardly.base.rating.models.RateInput
import io.reactivex.Observable

interface RateView {

    fun ratingEmitter(): Observable<RateInput>

    fun emitRating(rateInput: RateInput)
}