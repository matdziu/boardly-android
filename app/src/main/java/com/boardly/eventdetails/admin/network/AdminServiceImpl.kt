package com.boardly.eventdetails.admin.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.players.models.Player
import com.boardly.constants.ACCEPTED_EVENTS_NODE
import com.boardly.constants.PENDING_EVENTS_NODE
import io.reactivex.Observable

class AdminServiceImpl : BaseServiceImpl(), AdminService {

    override fun getAcceptedPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(getPlayersNode(eventId).child(ACCEPTED_EVENTS_NODE))
                .flattenAsObservable { it }
                .flatMapSingle { completePlayerProfile(it) }
                .toList()
                .toObservable()
    }

    override fun getPendingPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(getPlayersNode(eventId).child(PENDING_EVENTS_NODE))
                .flattenAsObservable { it }
                .flatMapSingle { completePlayerProfile(it) }
                .toList()
                .toObservable()
    }
}