package com.boardly.eventdetails.chat.network

import com.boardly.eventdetails.chat.models.RawMessage
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

abstract class NewMessageAddedListener : ChildEventListener {

    abstract fun onNewMessageAdded(newMessage: RawMessage)

    override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
        dataSnapshot.getValue(RawMessage::class.java)?.let { onNewMessageAdded(it) }
    }

    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        // unused
    }

    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
        // unused
    }

    override fun onChildRemoved(p0: DataSnapshot) {
        // unused
    }

    override fun onCancelled(p0: DatabaseError) {
        // unused
    }
}