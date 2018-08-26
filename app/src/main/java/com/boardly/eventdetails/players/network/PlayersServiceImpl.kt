package com.boardly.eventdetails.players.network

import com.boardly.base.rating.network.RateServiceImpl
import com.boardly.common.players.models.Player
import io.reactivex.Observable

class PlayersServiceImpl : RateServiceImpl(), PlayersService {

    override val userId: String = currentUserId

    override fun getAcceptedPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(getAcceptedPlayersNode(eventId))
                .flatMap { completePlayerProfilesWithRating(it, eventId) }
    }
}