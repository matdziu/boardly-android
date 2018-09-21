package com.boardly.base.eventdetails.network

import com.boardly.base.BaseServiceImpl
import com.boardly.base.eventdetails.models.RateInput
import com.boardly.common.events.models.Event
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class EventDetailsServiceImpl : EventDetailsService, BaseServiceImpl() {

    override fun fetchEventDetails(eventId: String): Observable<Event> {
        val resultSubject = PublishSubject.create<Event>()

        getSingleEventNode(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(Event::class.java)?.let {
                    it.eventId = eventId
                    resultSubject.onNext(it)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onError(databaseError.toException())
            }
        })

        return resultSubject
    }

    override fun sendRating(rateInput: RateInput): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        with(rateInput) {
            getUserRatingHashesRef(playerId)
                    .child(eventId + currentUserId)
                    .setValue(rating)
                    .addOnSuccessListener { resultSubject.onNext(true) }
        }

        return resultSubject
    }

    override fun removePlayer(eventId: String, playerId: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        deleteNodesTask(getUserAcceptedEventsNodeRef(playerId),
                getAcceptedPlayersNode(eventId),
                eventId,
                playerId)
                .addOnSuccessListener { resultSubject.onNext(true) }

        return resultSubject
    }

    fun deleteNodesTask(userEventsNode: DatabaseReference,
                        eventUsersNode: DatabaseReference,
                        eventId: String,
                        userId: String): Task<List<Task<*>>> {
        return Tasks.whenAllComplete(
                deleteEventFromUsersNodeTask(userEventsNode, eventId),
                deleteUserFromEventsNodeTask(eventUsersNode, userId))
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