package com.boardly.base

import android.support.v4.app.Fragment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class BaseRateFragment : Fragment(), RateView {

    private lateinit var ratingSubject: PublishSubject<Int>

    open fun initEmitters() {
        ratingSubject = PublishSubject.create()
    }

    override fun ratingEmitter(): Observable<Int> = ratingSubject

    override fun emitRating(rating: Int) {
        ratingSubject.onNext(rating)
    }
}