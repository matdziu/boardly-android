package com.boardly.event.network

import com.boardly.event.InputData
import io.reactivex.Observable

interface EventService {

    fun addEvent(inputData: InputData): Observable<Boolean>

    fun editEvent(inputData: InputData): Observable<Boolean>

    fun deleteEvent(eventId: String): Observable<Boolean>
}