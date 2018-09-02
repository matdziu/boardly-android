package com.boardly.base.eventdetails.network

import com.boardly.base.BaseServiceImpl
import com.boardly.base.eventdetails.models.RateInput
import com.boardly.common.events.models.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class EventDetailsServiceImpl : EventDetailsService, BaseServiceImpl() {

    override fun fetchEventDetails(eventId: String): Observable<Event> {
        val resultSubject = PublishSubject.create<Event>()

        getSingleEventNode(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(Event::class.java)?.let {
                    it.eventId = eventId
                    resultSubject.onNext(it)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onError(databaseError.toException())
            }
        })

        return resultSubject
    }

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