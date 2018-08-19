package com.boardly.base.rating.network

import com.boardly.base.rating.models.RateInput
import io.reactivex.Observable

interface RateService {

    fun sendRating(rateInput: RateInput): Observable<Boolean>
}