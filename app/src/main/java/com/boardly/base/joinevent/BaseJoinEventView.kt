package com.boardly.base.joinevent

import com.boardly.home.models.JoinEventData
import io.reactivex.Observable

interface BaseJoinEventView {

    fun joinEventEmitter(): Observable<JoinEventData>
}