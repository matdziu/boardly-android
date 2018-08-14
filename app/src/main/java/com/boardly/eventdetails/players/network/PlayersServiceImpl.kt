package com.boardly.eventdetails.players.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.events.models.Player
import com.boardly.constants.ACCEPTED_EVENTS_NODE
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PlayersServiceImpl : BaseServiceImpl(), PlayersService {

    override fun getAcceptedPlayers(eventId: String): Observable<List<Player>> {
        val resultSubject = PublishSubject.create<List<Player>>()

        getPartialPlayerProfiles(eventId)
                .continueWithTask { completePlayerProfiles(it.result) }
                .addOnSuccessListener { resultSubject.onNext(it) }

        return resultSubject
    }

    private fun getPartialPlayerProfiles(eventId: String): Task<List<Player>> {
        val dbSource = TaskCompletionSource<List<Player>>()
        val dbTask = dbSource.task

        getPlayersNode(eventId).child(ACCEPTED_EVENTS_NODE).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val partialPlayersList = arrayListOf<Player>()
                for (childSnapshot in dataSnapshot.children) {
                    partialPlayersList.add(Player(id = childSnapshot.key.orEmpty(), helloText = childSnapshot.value.toString()))
                }
                dbSource.setResult(partialPlayersList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dbSource.setException(databaseError.toException())
            }
        })

        return dbTask
    }

    private fun completePlayerProfiles(partialPlayersList: List<Player>): Task<List<Player>> {
        val dbSource = TaskCompletionSource<List<Player>>()
        val dbTask = dbSource.task
        val playersList = arrayListOf<Player>()

        if (partialPlayersList.isEmpty()) dbSource.setResult(playersList)

        for (partialPlayer in partialPlayersList) {
            getUserNodeRef(partialPlayer.id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(Player::class.java)?.let {
                        it.helloText = partialPlayer.helloText
                        it.id = partialPlayer.id
                        playersList.add(it)
                        if (playersList.size == partialPlayersList.size) dbSource.setResult(playersList)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    dbSource.setException(databaseError.toException())
                }
            })
        }

        return dbTask
    }
}