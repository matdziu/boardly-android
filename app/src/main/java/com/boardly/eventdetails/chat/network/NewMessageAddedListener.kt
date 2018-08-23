package com.boardly.eventdetails.chat.network

import com.boardly.eventdetails.chat.list.Message
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot

abstract class NewMessageAddedListener : ChildEventListener {

    abstract fun onNewMessageAdded(newMessage: Message)

    override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
        dataSnapshot.getValue(Message::class.java)?.let { onNewMessageAdded(it) }
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
}