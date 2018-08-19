package com.boardly.eventdetails.admin.network

import com.boardly.base.rating.network.RateService
import com.boardly.common.players.models.Player
import io.reactivex.Observable

interface AdminService : RateService {

    fun getAcceptedPlayers(eventId: String): Observable<List<Player>>

    fun getPendingPlayers(eventId: String): Observable<List<Player>>

    fun acceptPlayer(eventId: String, playerId: String): Observable<Boolean>

    fun kickPlayer(eventId: String, playerId: String): Observable<Boolean>
}