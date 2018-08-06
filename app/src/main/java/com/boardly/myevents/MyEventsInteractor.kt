package com.boardly.myevents

import com.boardly.base.BaseInteractor
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable

class MyEventsInteractor : BaseInteractor() {

    fun fetchEvents(): Observable<PartialMyEventsViewState> {

    }

    private fun pendingEventIdsList(): Task<List<String>> {
        return idsList(getUserPendingEventsNodeRef(currentUserId))
    }

    private fun acceptedEventIdsList(): Task<List<String>> {
        return idsList(getUserAcceptedEventsNodeRef(currentUserId))
    }

    private fun mineEventIdsList(): Task<List<String>> {
        return idsList(getUserMineEventsNodeRef(currentUserId))
    }

    private fun idsList(idsDatabaseReference: DatabaseReference): Task<List<String>> {
        val dbSource = TaskCompletionSource<List<String>>()
        val dbTask = dbSource.task

        idsDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val idsList = arrayListOf<String>()
                for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.getValue(String::class.java)?.let { idsList.add(it) }
                }
                dbSource.setResult(idsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                dbSource.setException(databaseError.toException())
            }
        })

        return dbTask
    }
}