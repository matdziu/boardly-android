package com.boardly.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.boardly.BoardlyApplication
import com.boardly.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

    private lateinit var boardlyApplication: BoardlyApplication

    override fun onCreate() {
        boardlyApplication = application as BoardlyApplication
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

    }

    private fun showDefaultNotification(pendingIntent: PendingIntent,
                                        title: String,
                                        body: String,
                                        channelId: String,
                                        notificationId: Int) {
        val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val notificationsManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationsManager.notify(notificationId, notification)
    }
}