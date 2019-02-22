package com.boardly.gamescollection.network

import com.boardly.base.BaseServiceImpl
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
                    childSnapshot.getValue(CollectionGame::class.java)?.let {
                        it.id = childSnapshot.key.orEmpty()
                        gamesCollectionList.add(it)
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

    override fun addGame(collectionId: String, game: CollectionGame): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        getSingleCollectionRef(collectionId).child(game.id)
                .setValue(game).addOnSuccessListener { resultSubject.onNext(true) }

        return resultSubject
    }

    override fun deleteGame(collectionId: String, gameId: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        getSingleCollectionRef(collectionId).child(gameId).setValue(null)
                .addOnSuccessListener { resultSubject.onNext(true) }

        return resultSubject
    }
}