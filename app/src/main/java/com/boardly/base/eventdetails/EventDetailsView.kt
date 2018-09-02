package com.boardly.base.eventdetails

import com.boardly.base.eventdetails.models.RateInput
import io.reactivex.Observable

interface EventDetailsView {

    fun ratingEmitter(): Observable<RateInput>

    fun emitRating(rateInput: RateInput)
}