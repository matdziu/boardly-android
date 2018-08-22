package com.boardly.eventdetails.chat.list

import android.support.annotation.LayoutRes
import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.R

class MessagesAdapter : ListAdapter<Message, MessageViewHolder>(MessagesDiffCallback()) {

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (MessageType.values()[viewType]) {
            MessageType.SENT -> SentMessageViewHolder(inflateItemView(R.layout.item_sent_message, parent))
            MessageType.RECEIVED -> ReceivedMessageViewHolder(inflateItemView(R.layout.item_received_message, parent))
        }
    }

    private fun inflateItemView(@LayoutRes resId: Int, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(resId, parent, false)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getLastTimestamp(): String = getItem(0).timestamp
}