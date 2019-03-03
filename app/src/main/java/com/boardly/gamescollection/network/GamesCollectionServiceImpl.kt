package com.boardly.gamescollection.network

import com.boardly.base.BaseServiceImpl
import com.boardly.constants.GAMES_LIMIT_CHILD
import com.boardly.gamescollection.models.CollectionGame
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class GamesCollectionServiceImpl : GamesCollectionService, BaseServiceImpl() {

    override fun fetchGames(collectionId: String): Observable<List<CollectionGame>> {
        val resultSubject = PublishSubject.create<List<CollectionGame>>()

        getSingleCollectionRef(collectionId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gamesCollectionList = arrayListOf<CollectionGame>()
                for (childSnapshot in dataSnapshot.children) {
                    try {
                        childSnapshot.getValue(CollectionGame::class.java)?.let {
                            it.id = childSnapshot.key.orEmpty()
                            gamesCollectionList.add(it)
                        }
                    } catch (e: Exception) {
                        // unused
                    }
                }
                resultSubject.onNext(gamesCollectionList)
            }

            override fun onCancelled(databasError: DatabaseError) {
                // unused
            }
        })

        return resultSubject
    }

    override fun addGame(collectionId: String,
                         game: CollectionGame,
                         currentCollectionCount: Int): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        getSingleCollectionRef(collectionId).child(GAMES_LIMIT_CHILD).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gamesLimit = dataSnapshot.getValue(Int::class.java) ?: 0
                if (currentCollectionCount + 1 <= gamesLimit) {
                    game.id = if (game.id.isEmpty()) getSingleCollectionRef(collectionId).push().key
                            ?: "" else game.id
                    getSingleCollectionRef(collectionId).child(game.id)
                            .setValue(game).addOnSuccessListener { resultSubject.onNext(true) }
                } else {
                    resultSubject.onNext(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // unused
            }
        })

        return resultSubject
    }

    override fun deleteGame(collectionId: String, gameId: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        getSingleCollectionRef(collectionId).child(gameId).setValue(null)
                .addOnSuccessListener { resultSubject.onNext(true) }

        return resultSubject
    }
}