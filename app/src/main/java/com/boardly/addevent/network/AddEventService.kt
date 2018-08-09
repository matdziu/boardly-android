package com.boardly.addevent.network

import com.boardly.addevent.InputData
import io.reactivex.Observable

interface AddEventService {

    fun addEvent(inputData: InputData): Observable<Boolean>
}