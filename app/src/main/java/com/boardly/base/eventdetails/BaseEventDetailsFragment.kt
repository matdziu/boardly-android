package com.boardly.base.eventdetails

import android.support.v4.app.Fragment
import com.boardly.base.eventdetails.models.RateInput
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class BaseEventDetailsFragment : Fragment(), EventDetailsView {

    private lateinit var ratingSubject: PublishSubject<RateInput>
    private lateinit var fetchEventTriggerSubject: PublishSubject<String>

    open fun initEmitters() {
        ratingSubject = PublishSubject.create()
        fetchEventTriggerSubject = PublishSubject.create()
    }

    override fun ratingEmitter(): Observable<RateInput> = ratingSubject

    override fun emitRating(rateInput: RateInput) {
        ratingSubject.onNext(rateInput)
    }
}