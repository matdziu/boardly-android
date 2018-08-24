package com.boardly.eventdetails.chat.list

import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.view.View
import com.boardly.R
import kotlinx.android.synthetic.main.item_sent_message.view.messageTextView

class SentMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {

    override fun bind(message: Message) {
        with(itemView) {
            messageTextView.text = message.text
            if (message.isSent) setMessageBackground(R.drawable.sent_message_background)
            else setMessageBackground(R.drawable.not_sent_message_background)
        }
    }

    private fun setMessageBackground(@DrawableRes resId: Int) {
        with(itemView) {
            messageTextView.background = ContextCompat.getDrawable(context, resId)
        }
    }
}