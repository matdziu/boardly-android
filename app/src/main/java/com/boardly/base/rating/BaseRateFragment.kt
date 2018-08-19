package com.boardly.base.rating

import android.support.v4.app.Fragment
import com.boardly.base.rating.models.RateInput
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class BaseRateFragment : Fragment(), RateView {

    private lateinit var ratingSubject: PublishSubject<RateInput>

    open fun initEmitters() {
        ratingSubject = PublishSubject.create()
    }

    override fun ratingEmitter(): Observable<RateInput> = ratingSubject

    override fun emitRating(rateInput: RateInput) {
        ratingSubject.onNext(rateInput)
    }
}