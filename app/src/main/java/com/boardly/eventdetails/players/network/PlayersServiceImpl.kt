package com.boardly.eventdetails.players.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.events.models.Player
import com.boardly.constants.ACCEPTED_EVENTS_NODE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PlayersServiceImpl : BaseServiceImpl(), PlayersService {

    override fun getAcceptedPlayers(eventId: String): Observable<List<Player>> {
        val resultSubject = PublishSubject.create<List<Player>>()

        getPlayersNode(eventId).child(ACCEPTED_EVENTS_NODE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        return resultSubject
    }
}