package com.boardly.eventdetails.admin.network

import com.boardly.common.players.models.Player
import io.reactivex.Observable

interface AdminService {

    fun getAcceptedPlayers(eventId: String): Observable<List<Player>>

    fun getPendingPlayers(eventId: String): Observable<List<Player>>
}