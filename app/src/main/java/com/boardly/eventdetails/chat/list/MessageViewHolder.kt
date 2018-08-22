package com.boardly.eventdetails.chat.list

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(message: Message)
}