package com.boardly.eventdetails.players.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.players.models.Player
import io.reactivex.Observable

class PlayersServiceImpl : BaseServiceImpl(), PlayersService {

    override fun getAcceptedPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(getAcceptedPlayersNode(eventId))
                .flatMap { completePlayerProfilesWithRating(it, eventId) }
    }
}