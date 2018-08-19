package com.boardly.base.rating.network

import com.boardly.base.BaseServiceImpl
import com.boardly.base.rating.models.RateInput
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class RateServiceImpl : RateService, BaseServiceImpl() {

    override fun sendRating(rateInput: RateInput): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        with(rateInput) {
            getUserRatingHashesRef(playerId)
                    .child(eventId + currentUserId)
                    .setValue(rating)
                    .addOnSuccessListener { resultSubject.onNext(true) }
        }

        return resultSubject
    }
}