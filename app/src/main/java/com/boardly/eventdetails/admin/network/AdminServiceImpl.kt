package com.boardly.eventdetails.admin.network

import com.boardly.base.eventdetails.network.EventDetailsServiceImpl
import com.boardly.common.players.models.Player
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DatabaseReference
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AdminServiceImpl : EventDetailsServiceImpl(), AdminService {

    override fun acceptPlayer(eventId: String, playerId: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        deleteNodesTask(
                getUserPendingEventsNodeRef(playerId),
                getPendingPlayersNode(eventId),
                eventId,
                playerId)
                .continueWithTask {
                    moveNodesTask(
                            it.result[1].result as String,
                            getUserAcceptedEventsNodeRef(playerId),
                            getAcceptedPlayersNode(eventId),
                            playerId,
                            eventId)
                }
                .addOnSuccessListener { resultSubject.onNext(true) }

        return resultSubject
    }

    override fun kickPlayer(eventId: String, playerId: String): Observable<Boolean> {
        return removePlayer(eventId, playerId)
    }

    override fun getAcceptedPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(getAcceptedPlayersNode(eventId))
                .flatMap { completePlayerProfilesWithRating(it, eventId) }
    }

    override fun getPendingPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(getPendingPlayersNode(eventId))
                .flatMap { completePlayerProfiles(it) }
    }

    private fun moveNodesTask(helloText: String,
                              userEventsNode: DatabaseReference,
                              eventUsersNode: DatabaseReference,
                              playerId: String,
                              eventId: String): Task<List<Task<*>>> {
        return Tasks.whenAllComplete(
                userEventsNode
                        .push()
                        .setValue(eventId),
                eventUsersNode
                        .child(playerId)
                        .setValue(helloText))
    }

}