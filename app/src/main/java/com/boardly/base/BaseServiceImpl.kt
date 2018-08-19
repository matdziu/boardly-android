package com.boardly.base

import com.boardly.common.players.models.Player
import com.boardly.constants.ACCEPTED_EVENTS_NODE
import com.boardly.constants.CREATED_EVENTS_NODE
import com.boardly.constants.EVENTS_NODE
import com.boardly.constants.PENDING_EVENTS_NODE
import com.boardly.constants.PLAYERS_NODE
import com.boardly.constants.USERS_NODE
import com.firebase.geofire.GeoFire
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class BaseServiceImpl {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    protected val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    protected val currentUserId = firebaseAuth.currentUser?.uid.orEmpty()

    protected fun getUserNodeRef(userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId")
    }

    protected fun getSingleEventNode(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$EVENTS_NODE/$eventId")
    }

    protected fun getAcceptedPlayersNode(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$PLAYERS_NODE/$eventId/$ACCEPTED_EVENTS_NODE")
    }

    protected fun getPendingPlayersNode(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$PLAYERS_NODE/$eventId/$PENDING_EVENTS_NODE")
    }

    protected fun getStorageProfilePictureRef(userId: String): StorageReference {
        return firebaseStorage.reference.child(userId)
    }

    protected fun getGeoFire(childPath: String): GeoFire {
        return GeoFire(firebaseDatabase.getReference(childPath))
    }

    protected fun getUserPendingEventsNodeRef(userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId/$EVENTS_NODE/$PENDING_EVENTS_NODE")
    }

    protected fun getUserAcceptedEventsNodeRef(userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId/$EVENTS_NODE/$ACCEPTED_EVENTS_NODE")
    }

    protected fun getUserCreatedEventsNodeRef(userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId/$EVENTS_NODE/$CREATED_EVENTS_NODE")
    }

    protected fun pendingEventIdsList(): Observable<List<String>> {
        return idsList(getUserPendingEventsNodeRef(currentUserId))
    }

    protected fun acceptedEventIdsList(): Observable<List<String>> {
        return idsList(getUserAcceptedEventsNodeRef(currentUserId))
    }

    protected fun createdEventIdsList(): Observable<List<String>> {
        return idsList(getUserCreatedEventsNodeRef(currentUserId))
    }

    private fun idsList(idsDatabaseReference: DatabaseReference): Observable<List<String>> {
        val resultSubject = PublishSubject.create<List<String>>()

        idsDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val idsList = arrayListOf<String>()
                for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.getValue(String::class.java)?.let { idsList.add(it) }
                }
                resultSubject.onNext(idsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onError(databaseError.toException())
            }
        })

        return resultSubject
    }

    protected fun getPartialPlayerProfiles(databaseReference: DatabaseReference)
            : Observable<List<Player>> {
        val resultSubject = PublishSubject.create<List<Player>>()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val partialPlayersList = arrayListOf<Player>()
                for (childSnapshot in dataSnapshot.children) {
                    partialPlayersList.add(Player(id = childSnapshot.key.orEmpty(), helloText = childSnapshot.value.toString()))
                }
                resultSubject.onNext(partialPlayersList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onError(databaseError.toException())
            }
        })

        return resultSubject
    }

    protected fun completePlayerProfiles(partialPlayersList: List<Player>): Observable<List<Player>> {
        val resultSubject = PublishSubject.create<List<Player>>()
        val playersList = arrayListOf<Player>()

        if (partialPlayersList.isEmpty()) return Observable.just(playersList)

        for (partialPlayer in partialPlayersList) {
            getUserNodeRef(partialPlayer.id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(Player::class.java)?.let {
                        it.helloText = partialPlayer.helloText
                        it.id = partialPlayer.id
                        playersList.add(it)
                        if (partialPlayersList.size == playersList.size) resultSubject.onNext(playersList)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    resultSubject.onError(databaseError.toException())
                }
            })
        }

        return resultSubject
    }
}