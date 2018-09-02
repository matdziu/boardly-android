package com.boardly.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import com.boardly.R
import com.boardly.constants.ACCEPTED_REQUEST_CHANNEL_ID
import com.boardly.constants.NEW_CHAT_MESSAGE_CHANNEL_ID
import com.boardly.constants.NEW_JOIN_REQUEST_CHANNEL_ID

class NotificationChannelsBuilder(private val context: Context) {

    @RequiresApi(26)
    fun buildChannels() {
        buildDefaultChannel(NEW_CHAT_MESSAGE_CHANNEL_ID, R.string.new_chat_message_channel)
        buildDefaultChannel(NEW_JOIN_REQUEST_CHANNEL_ID, R.string.new_join_request_channel)
        buildDefaultChannel(ACCEPTED_REQUEST_CHANNEL_ID, R.string.accepted_request_channel)
    }

    @RequiresApi(26)
    private fun buildDefaultChannel(channelId: String, @StringRes channelTitleRes: Int) {
        val notificationsManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, context.getString(channelTitleRes),
                NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = Color.BLUE
        channel.importance = NotificationManager.IMPORTANCE_HIGH
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        notificationsManager.createNotificationChannel(channel)
    }
}