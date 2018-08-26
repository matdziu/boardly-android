package com.boardly.eventdetails.chat.network

import com.boardly.base.BaseServiceImpl
import com.boardly.constants.TIMESTAMP_CHILD
import com.boardly.editprofile.models.ProfileData
import com.boardly.eventdetails.chat.models.RawMessage
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ChatServiceImpl : ChatService, BaseServiceImpl() {

    private var newMessageAddedListener: NewMessageAddedListener? = null

    private val batchSize = 10

    override val userId: String = currentUserId

    override fun listenForNewMessages(eventId: String): Observable<RawMessage> {
        val resultSubject = PublishSubject.create<RawMessage>()

        newMessageAddedListener = object : NewMessageAddedListener() {
            override fun onNewMessageAdded(newMessage: RawMessage) {
                completeRawMessageTask(newMessage).addOnSuccessListener { resultSubject.onNext(it) }
            }
        }

        getChatNodeReference(eventId).addChildEventListener(newMessageAddedListener as NewMessageAddedListener)

        return resultSubject
    }

    override fun stopListeningForNewMessages(eventId: String): Observable<Boolean> {
        newMessageAddedListener?.let { getChatNodeReference(eventId).removeEventListener(it) }
        return Observable.just(true)
    }

    override fun fetchMessagesBatch(eventId: String, fromTimestamp: String): Observable<List<RawMessage>> {
        return fetchPartialMessagesBatch(eventId, fromTimestamp)
                .flatMap { completeRawMessages(it) }
    }

    override fun sendMessage(rawMessage: RawMessage, eventId: String) {
        getChatNodeReference(eventId).push().setValue(rawMessage)
    }

    private fun fetchPartialMessagesBatch(eventId: String, fromTimestamp: String): Observable<List<RawMessage>> {
        val resultSubject = PublishSubject.create<List<RawMessage>>()

        getChatNodeReference(eventId)
                .orderByChild(TIMESTAMP_CHILD)
                .endAt(fromTimestamp)
                .limitToLast(batchSize)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val messagesBatchList = arrayListOf<RawMessage>()
                        for (messageSnapshot in dataSnapshot.children.toList()) {
                            messageSnapshot.getValue(RawMessage::class.java)?.let { messagesBatchList.add(it) }
                        }
                        resultSubject.onNext(messagesBatchList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        resultSubject.onError(databaseError.toException())
                    }
                })

        return resultSubject
    }

    private fun completeRawMessages(partialMessagesList: List<RawMessage>)
            : Observable<List<RawMessage>> {
        val resultSubject = PublishSubject.create<List<RawMessage>>()
        val completedMessagesList = arrayListOf<RawMessage>()

        if (partialMessagesList.isEmpty()) return Observable.just(completedMessagesList)

        for (partialMessage in partialMessagesList) {
            completeRawMessageTask(partialMessage)
                    .addOnSuccessListener {
                        completedMessagesList.add(it)
                        if (partialMessagesList.size == completedMessagesList.size) resultSubject.onNext(completedMessagesList)
                    }
        }

        return resultSubject
    }

    private fun completeRawMessageTask(partialMessage: RawMessage): Task<RawMessage> {
        val dbSource = TaskCompletionSource<RawMessage>()
        val dbTask = dbSource.task

        getUserNodeRef(partialMessage.senderId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(ProfileData::class.java)?.let {
                    partialMessage.senderName = it.name
                    partialMessage.senderImageUrl = it.profilePicture
                }
                dbSource.setResult(partialMessage)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dbSource.setException(databaseError.toException())
            }
        })

        return dbTask
    }
}