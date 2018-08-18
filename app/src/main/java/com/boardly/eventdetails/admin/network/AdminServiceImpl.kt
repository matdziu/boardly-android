package com.boardly.eventdetails.admin.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.players.models.Player
import com.boardly.constants.ACCEPTED_EVENTS_NODE
import com.boardly.constants.PENDING_EVENTS_NODE
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AdminServiceImpl : BaseServiceImpl(), AdminService {

    override fun acceptPlayer(eventId: String, playerId: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        deleteNodesTask(
                getUserPendingEventsNodeRef(playerId),
                getPlayersNode(eventId).child(PENDING_EVENTS_NODE),
                eventId,
                playerId)
                .continueWithTask {
                    moveNodesTask(
                            it.result[1].result as String,
                            getUserAcceptedEventsNodeRef(playerId),
                            getPlayersNode(eventId).child(ACCEPTED_EVENTS_NODE),
                            playerId,
                            eventId)
                }
                .addOnSuccessListener { resultSubject.onNext(true) }

        return resultSubject
    }

    override fun kickPlayer(eventId: String, playerId: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        deleteNodesTask(getUserAcceptedEventsNodeRef(playerId),
                getPlayersNode(eventId).child(ACCEPTED_EVENTS_NODE),
                eventId,
                playerId)
                .addOnSuccessListener { resultSubject.onNext(true) }

        return resultSubject
    }

    override fun getAcceptedPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(getPlayersNode(eventId).child(ACCEPTED_EVENTS_NODE))
                .flatMap { completePlayerProfiles(it) }
    }

    override fun getPendingPlayers(eventId: String): Observable<List<Player>> {
        return getPartialPlayerProfiles(getPlayersNode(eventId).child(PENDING_EVENTS_NODE))
                .flatMap { completePlayerProfiles(it) }
    }

    private fun deleteNodesTask(userEventsNode: DatabaseReference,
                                eventUsersNode: DatabaseReference,
                                eventId: String,
                                userId: String): Task<List<Task<*>>> {
        return Tasks.whenAllComplete(
                deleteEventFromUsersNodeTask(userEventsNode, eventId),
                deleteUserFromEventsNodeTask(eventUsersNode, userId))
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

    private fun deleteEventFromUsersNodeTask(userEventsNode: DatabaseReference,
                                             eventId: String): Task<Any> {
        val dbSource = TaskCompletionSource<Any>()
        val dbTask = dbSource.task

        userEventsNode.orderByValue()
                .equalTo(eventId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.ref.removeValue().addOnSuccessListener { dbSource.setResult(Any()) }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        dbSource.setException(databaseError.toException())
                    }
                })

        return dbTask
    }

    private fun deleteUserFromEventsNodeTask(eventUsersNode: DatabaseReference,
                                             userId: String): Task<String> {
        val dbSource = TaskCompletionSource<String>()
        val dbTask = dbSource.task

        eventUsersNode.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val helloText = dataSnapshot.getValue(String::class.java)
                        dataSnapshot.ref.removeValue().addOnSuccessListener { dbSource.setResult(helloText) }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        dbSource.setException(databaseError.toException())
                    }
                })

        return dbTask
    }
}