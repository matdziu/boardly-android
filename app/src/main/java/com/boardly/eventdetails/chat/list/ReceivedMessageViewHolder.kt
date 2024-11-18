package com.boardly.eventdetails.chat.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.boardly.R
import com.boardly.extensions.loadImageFromUrl

class ReceivedMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {

    override fun bind(message: Message) {
        with(itemView) {
            val messageTextView = this.findViewById<TextView>(R.id.messageTextView)
            val nameTextView = this.findViewById<TextView>(R.id.nameTextView)
            val playerImageView = this.findViewById<ImageView>(R.id.playerImageView)
            messageTextView.text = message.text
            nameTextView.text = message.senderName
            context.loadImageFromUrl(
                playerImageView,
                message.senderImageUrl,
                R.drawable.profile_picture_shape
            )
        }
    }
}