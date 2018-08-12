package com.boardly.eventdetails.players.network

import com.boardly.common.events.models.Player
import io.reactivex.Observable

interface PlayersService {

    fun getAcceptedPlayers(eventId: String): Observable<List<Player>>
}