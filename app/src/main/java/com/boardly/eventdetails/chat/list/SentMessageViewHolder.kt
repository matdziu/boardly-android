package com.boardly.eventdetails.chat.list

import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.boardly.R

class SentMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {

    override fun bind(message: Message) {
        with(itemView) {
            val messageTextView = this.findViewById<TextView>(R.id.messageTextView)
            messageTextView.text = message.text
            if (message.isSent) setMessageBackground(R.drawable.sent_message_background)
            else setMessageBackground(R.drawable.not_sent_message_background)
        }
    }

    private fun setMessageBackground(@DrawableRes resId: Int) {
        with(itemView) {
            val messageTextView = this.findViewById<TextView>(R.id.messageTextView)
            messageTextView.background = ContextCompat.getDrawable(context, resId)
        }
    }
}