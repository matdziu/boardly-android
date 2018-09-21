package com.boardly.base.eventdetails.network

import com.boardly.base.eventdetails.models.RateInput
import com.boardly.common.events.models.Event
import io.reactivex.Observable

interface EventDetailsService {

    fun sendRating(rateInput: RateInput): Observable<Boolean>

    fun fetchEventDetails(eventId: String): Observable<Event>

    fun removePlayer(eventId: String, playerId: String): Observable<Boolean>
}