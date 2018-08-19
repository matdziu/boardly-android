package com.boardly.eventdetails.players.network

import com.boardly.base.rating.network.RateService
import com.boardly.common.players.models.Player
import io.reactivex.Observable

interface PlayersService : RateService {

    fun getAcceptedPlayers(eventId: String): Observable<List<Player>>
}