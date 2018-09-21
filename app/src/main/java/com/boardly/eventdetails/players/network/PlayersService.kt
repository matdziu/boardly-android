package com.boardly.eventdetails.players.network

import com.boardly.base.eventdetails.network.EventDetailsService
import com.boardly.common.players.models.Player
import io.reactivex.Observable

interface PlayersService : EventDetailsService {

    val userId: String

    fun getAcceptedPlayers(eventId: String): Observable<List<Player>>

    fun leaveEvent(eventId: String): Observable<Boolean>
}