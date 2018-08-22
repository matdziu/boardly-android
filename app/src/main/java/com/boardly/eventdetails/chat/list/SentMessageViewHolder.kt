package com.boardly.eventdetails.chat.list

import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.view.View
import com.boardly.R
import kotlinx.android.synthetic.main.item_sent_message.view.messageTextView

class SentMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {

    override fun bind(message: Message) {
        with(message) {
            itemView.messageTextView.text = text
            if (type == MessageType.SENT) setMessageBackground(R.drawable.round_full_background_blue)
            else setMessageBackground(R.drawable.round_full_dim_blue_background)
        }
    }

    private fun setMessageBackground(@DrawableRes resId: Int) {
        with(itemView) {
            messageTextView.background = ContextCompat.getDrawable(context, resId)
        }
    }
}