package com.boardly.eventdetails.players.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.players.models.Player
import com.boardly.constants.ACCEPTED_EVENTS_NODE
import io.reactivex.Observable

class PlayersServiceImpl : BaseServiceImpl(), PlayersService {

    override fun getAcceptedPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(getPlayersNode(eventId).child(ACCEPTED_EVENTS_NODE))
                .flatMap { completePlayerProfiles(it) }
    }
}