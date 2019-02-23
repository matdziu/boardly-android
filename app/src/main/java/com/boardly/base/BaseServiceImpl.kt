package com.boardly.base

import com.boardly.common.events.models.Event
import com.boardly.common.players.models.Player
import com.boardly.constants.ACCEPTED_EVENTS_NODE
import com.boardly.constants.CHATS_NODE
import com.boardly.constants.COLLECTIONS_NODE
import com.boardly.constants.CREATED_EVENTS_NODE
import com.boardly.constants.EVENTS_NODE
import com.boardly.constants.EVENTS_WITH_INTEREST
import com.boardly.constants.INTERESTING_EVENTS_NODE
import com.boardly.constants.NOTIFY_SETTINGS_NODE
import com.boardly.constants.PENDING_EVENTS_NODE
import com.boardly.constants.PLACES_NODE
import com.boardly.constants.PLAYERS_NODE
import com.boardly.constants.RATING_HASHES
import com.boardly.constants.USERS_NODE
import com.boardly.home.models.JoinEventData
import com.firebase.geofire.GeoFire
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
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

    protected val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    protected val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    protected val currentUserId = firebaseAuth.currentUser?.uid.orEmpty()

    protected fun getChatNodeReference(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$CHATS_NODE/$eventId")
    }

    protected fun getUserNodeRef(userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId")
    }

    protected fun getEventsWithInterestRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$EVENTS_WITH_INTEREST/$eventId")
    }

    protected fun getUserRatingHashesRef(userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId/$RATING_HASHES")
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

    protected fun getStoragePlacePictureRef(placeId: String): StorageReference {
        return firebaseStorage.reference.child(placeId)
    }

    protected fun getSingleCollectionRef(collectionId: String): DatabaseReference {
        return firebaseDatabase.getReference("$COLLECTIONS_NODE/$collectionId")
    }

    protected fun getSinglePlaceRef(placeId: String): DatabaseReference {
        return firebaseDatabase.getReference("$PLACES_NODE/$placeId")
    }

    protected fun getPlacesRef(): DatabaseReference {
        return firebaseDatabase.getReference(PLACES_NODE)
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

    protected fun getUserInterestingEventsNodeRef(userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId/$EVENTS_NODE/$INTERESTING_EVENTS_NODE")
    }

    protected fun getUserNotifySettingsRef(userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$NOTIFY_SETTINGS_NODE/$userId")
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

    protected fun interestingEventIdsList(): Observable<List<String>> {
        return idsList(getUserInterestingEventsNodeRef(currentUserId))
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
                resultSubject.onNext(listOf())
            }
        })

        return resultSubject
    }

    protected fun getKeysTask(databaseReference: DatabaseReference): Task<List<String>> {
        val dbSource = TaskCompletionSource<List<String>>()
        val dbTask = dbSource.task

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val keysList = arrayListOf<String>()
                for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.key?.let { keysList.add(it) }
                }
                dbSource.setResult(keysList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dbSource.setResult(listOf())
            }
        })

        return dbTask
    }

    protected fun getValuesTask(databaseReference: DatabaseReference): Task<List<String>> {
        val dbSource = TaskCompletionSource<List<String>>()
        val dbTask = dbSource.task

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val valuesList = arrayListOf<String>()
                for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.getValue(String::class.java)?.let { valuesList.add(it) }
                }
                dbSource.setResult(valuesList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dbSource.setResult(listOf())
            }
        })

        return dbTask
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
                resultSubject.onNext(listOf())
            }
        })

        return resultSubject
    }

    protected fun completePlayerProfilesWithRating(partialPlayersList: List<Player>, eventId: String)
            : Observable<List<Player>> {
        val resultSubject = PublishSubject.create<List<Player>>()
        val playersList = arrayListOf<Player>()
        val currentRatingHash = eventId + currentUserId

        if (partialPlayersList.isEmpty()) return Observable.just(playersList)

        for (partialPlayer in partialPlayersList) {
            completePlayerProfileTask(partialPlayer)
                    .continueWithTask { checkIfRatedOrSelfTask(it.result, currentRatingHash) }
                    .addOnSuccessListener {
                        it.eventId = eventId
                        playersList.add(it)
                        if (partialPlayersList.size == playersList.size) resultSubject.onNext(playersList)
                    }
        }

        return resultSubject
    }

    private fun completePlayerProfileTask(partialPlayer: Player): Task<Player> {
        val dbSource = TaskCompletionSource<Player>()
        val dbTask = dbSource.task

        getUserNodeRef(partialPlayer.id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val player = dataSnapshot.getValue(Player::class.java)?.apply {
                    helloText = partialPlayer.helloText
                    id = partialPlayer.id
                }
                dbSource.setResult(player)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dbSource.setResult(Player())
            }
        })

        return dbTask
    }

    private fun checkIfRatedOrSelfTask(player: Player, currentRatingHash: String): Task<Player> {
        val dbSource = TaskCompletionSource<Player>()
        val dbTask = dbSource.task

        if (player.id == currentUserId) {
            dbSource.setResult(player.apply { ratedOrSelf = true })
            return dbTask
        }

        getUserRatingHashesRef(player.id)
                .child(currentRatingHash)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dbSource.setResult(player.apply { ratedOrSelf = dataSnapshot.value != null })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        dbSource.setResult(Player())
                    }
                })

        return dbTask
    }

    protected fun completePlayerProfiles(partialPlayersList: List<Player>)
            : Observable<List<Player>> {
        val resultSubject = PublishSubject.create<List<Player>>()
        val playersList = arrayListOf<Player>()

        if (partialPlayersList.isEmpty()) return Observable.just(playersList)

        for (partialPlayer in partialPlayersList) {
            completePlayerProfileTask(partialPlayer)
                    .addOnSuccessListener {
                        playersList.add(it)
                        if (partialPlayersList.size == playersList.size) resultSubject.onNext(playersList)
                    }
        }

        return resultSubject
    }

    protected fun events(idsList: List<String>): Observable<List<Event>> {
        val resultSubject = PublishSubject.create<List<Event>>()
        val eventList = arrayListOf<Event>()

        if (idsList.isEmpty()) return Observable.just(eventList)

        for (id in idsList) {
            getSingleEventNode(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(Event::class.java)?.let {
                        it.eventId = id
                        eventList.add(0, it)
                    }
                    if (eventList.size == idsList.size) resultSubject.onNext(eventList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    resultSubject.onError(databaseError.toException())
                }
            })
        }

        return resultSubject
    }

    fun sendJoinRequest(joinEventData: JoinEventData): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        Tasks.whenAllComplete(
                getUserPendingEventsNodeRef(currentUserId)
                        .push()
                        .setValue(joinEventData.eventId),
                getPendingPlayersNode(joinEventData.eventId)
                        .child(currentUserId)
                        .setValue(joinEventData.helloText))
                .addOnSuccessListener { resultSubject.onNext(true) }
        return resultSubject
    }
}