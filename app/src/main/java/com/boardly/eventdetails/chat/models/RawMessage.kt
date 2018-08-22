package com.boardly.eventdetails.chat.models

import com.boardly.eventdetails.chat.list.Message
import com.boardly.eventdetails.chat.list.MessageType

data class RawMessage(val id: String = "",
                      val text: String = "",
                      val senderId: String = "",
                      val timestamp: String = "",
                      var senderName: String = "",
                      var senderImageUrl: String = "") {

    fun toMessage(currentUserId: String): Message {
        return Message(
                id = id,
                text = text,
                type = if (senderId == currentUserId) MessageType.SENT else MessageType.RECEIVED,
                isSent = true,
                senderName = senderName,
                senderImageUrl = senderImageUrl,
                timestamp = timestamp)
    }
}