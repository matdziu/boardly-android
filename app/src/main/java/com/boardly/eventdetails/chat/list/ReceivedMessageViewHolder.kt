package com.boardly.eventdetails.chat.list

import android.view.View
import com.boardly.R
import com.boardly.extensions.loadImageFromUrl
import kotlinx.android.synthetic.main.item_received_message.view.messageTextView
import kotlinx.android.synthetic.main.item_received_message.view.nameTextView
import kotlinx.android.synthetic.main.item_received_message.view.playerImageView

class ReceivedMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {

    override fun bind(message: Message) {
        with(itemView) {
            messageTextView.text = message.text
            nameTextView.text = message.senderName
            context.loadImageFromUrl(playerImageView, message.senderImageUrl, R.drawable.profile_picture_shape)
        }
    }
}