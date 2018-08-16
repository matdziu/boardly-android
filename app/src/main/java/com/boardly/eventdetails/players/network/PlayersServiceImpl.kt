package com.boardly.eventdetails.players.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.events.models.Player
import com.boardly.constants.ACCEPTED_EVENTS_NODE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject

class PlayersServiceImpl : BaseServiceImpl(), PlayersService {

    override fun getAcceptedPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(eventId)
                .flattenAsObservable { it }
                .flatMapSingle { completePlayerProfile(it) }
                .toList()
                .toObservable()
    }

    private fun getPartialPlayerProfiles(eventId: String): Single<List<Player>> {
        val resultSubject = SingleSubject.create<List<Player>>()

        getPlayersNode(eventId).child(ACCEPTED_EVENTS_NODE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val partialPlayersList = arrayListOf<Player>()
                for (childSnapshot in dataSnapshot.children) {
                    partialPlayersList.add(Player(id = childSnapshot.key.orEmpty(), helloText = childSnapshot.value.toString()))
                }
                resultSubject.onSuccess(partialPlayersList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onError(databaseError.toException())
            }
        })

        return resultSubject
    }

    private fun completePlayerProfile(partialPlayer: Player): Single<Player> {
        val resultSubject = SingleSubject.create<Player>()

        getUserNodeRef(partialPlayer.id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(Player::class.java)?.let {
                    it.helloText = partialPlayer.helloText
                    it.id = partialPlayer.id
                    resultSubject.onSuccess(it)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onError(databaseError.toException())
            }
        })

        return resultSubject
    }
}