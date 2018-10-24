package com.boardly.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.boardly.BoardlyApplication
import com.boardly.R
import com.boardly.common.events.models.EventType
import com.boardly.constants.ACCEPTED_REQUEST_CHANNEL_ID
import com.boardly.constants.ACCEPTED_REQUEST_NOTIFICATION_TYPE
import com.boardly.constants.ACCEPTED_REQUEST_REQUEST_CODE
import com.boardly.constants.EVENT_ID
import com.boardly.constants.EVENT_TYPE
import com.boardly.constants.EVENT_TYPE_ADMIN
import com.boardly.constants.EVENT_TYPE_PLAYER
import com.boardly.constants.EXTRAS_EVENT_ID
import com.boardly.constants.EXTRAS_EVENT_TYPE
import com.boardly.constants.LAUNCH_INFO
import com.boardly.constants.NEW_CHAT_MESSAGE_CHANNEL_ID
import com.boardly.constants.NEW_CHAT_MESSAGE_NOTIFICATION_TYPE
import com.boardly.constants.NEW_CHAT_MESSAGE_REQUEST_CODE
import com.boardly.constants.NEW_EVENT_CHANNEL_ID
import com.boardly.constants.NEW_EVENT_NOTIFICATION_TYPE
import com.boardly.constants.NEW_EVENT_REQUEST_CODE
import com.boardly.constants.NEW_JOIN_REQUEST_CHANNEL_ID
import com.boardly.constants.NEW_JOIN_REQUEST_NOTIFICATION_TYPE
import com.boardly.constants.NEW_JOIN_REQUEST_REQUEST_CODE
import com.boardly.constants.NOTIFICATION_BODY_ARGS
import com.boardly.constants.NOTIFICATION_EXTRAS_KEY
import com.boardly.constants.NOTIFICATION_TITLE_ARGS
import com.boardly.constants.NOTIFICATION_TYPE_KEY
import com.boardly.constants.TOGGLE_CHAT_NOTIFICATIONS_KEY_PREFIX
import com.boardly.eventdetails.EventDetailsActivity
import com.boardly.extensions.jsonToArrayOfStrings
import com.boardly.extensions.jsonToMapOfStrings
import com.boardly.extensions.readAppSetting
import com.boardly.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

    private lateinit var boardlyApplication: BoardlyApplication

    override fun onCreate() {
        boardlyApplication = application as BoardlyApplication
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val titleArgs = remoteMessage.data[NOTIFICATION_TITLE_ARGS].jsonToArrayOfStrings()
        val bodyArgs = remoteMessage.data[NOTIFICATION_BODY_ARGS].jsonToArrayOfStrings()
        val extras = remoteMessage.data[NOTIFICATION_EXTRAS_KEY].jsonToMapOfStrings()
        val notificationType = remoteMessage.data[NOTIFICATION_TYPE_KEY]

        val eventId = extras[EXTRAS_EVENT_ID] ?: ""
        val visibleActivity = boardlyApplication.visibleActivity

        if (visibleActivity !is EventDetailsActivity || visibleActivity.eventId != eventId) {
            when (notificationType) {
                NEW_CHAT_MESSAGE_NOTIFICATION_TYPE -> handleChatMessageNotification(
                        titleArgs,
                        bodyArgs,
                        eventId,
                        extras[EXTRAS_EVENT_TYPE] ?: "")
                NEW_JOIN_REQUEST_NOTIFICATION_TYPE -> handleJoinRequestNotification(
                        titleArgs,
                        eventId)
                ACCEPTED_REQUEST_NOTIFICATION_TYPE -> handleAcceptedRequestNotification(
                        titleArgs,
                        eventId)
                NEW_EVENT_NOTIFICATION_TYPE -> handleNewEventNotification(
                        bodyArgs,
                        eventId)
            }
        }
    }

    private fun handleChatMessageNotification(titleArgs: Array<String>,
                                              bodyArgs: Array<String>,
                                              eventId: String,
                                              eventType: String) {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                        .readAppSetting("$TOGGLE_CHAT_NOTIFICATIONS_KEY_PREFIX$eventId")) {
            val title = getString(R.string.chat_message_notification_title, titleArgs[0])
            val body = getString(R.string.chat_message_notification_body, bodyArgs[0])
            val notificationId = eventId.hashCode() + NEW_CHAT_MESSAGE_REQUEST_CODE
            val eventTypeEnum = getEventType(eventType)
            val pendingIntent = createEventDetailsPendingIntent(eventId, eventTypeEnum, NEW_CHAT_MESSAGE_REQUEST_CODE)
            showDefaultNotification(pendingIntent, title, body, NEW_CHAT_MESSAGE_CHANNEL_ID, notificationId)
        }
    }

    private fun handleJoinRequestNotification(titleArgs: Array<String>,
                                              eventId: String) {
        val title = getString(R.string.join_request_notification_title, titleArgs[0])
        val body = getString(R.string.join_request_notification_body)
        val notificationId = eventId.hashCode() + NEW_JOIN_REQUEST_REQUEST_CODE
        val eventType = EventType.CREATED
        val pendingIntent = createEventDetailsPendingIntent(eventId, eventType, NEW_JOIN_REQUEST_REQUEST_CODE)
        showDefaultNotification(pendingIntent, title, body, NEW_JOIN_REQUEST_CHANNEL_ID, notificationId)
    }

    private fun handleAcceptedRequestNotification(titleArgs: Array<String>,
                                                  eventId: String) {
        val title = getString(R.string.accepted_request_notification_title, titleArgs[0])
        val body = getString(R.string.accepted_request_notification_body)
        val notificationId = eventId.hashCode() + ACCEPTED_REQUEST_REQUEST_CODE
        val eventType = EventType.ACCEPTED
        val pendingIntent = createEventDetailsPendingIntent(eventId, eventType, ACCEPTED_REQUEST_REQUEST_CODE)
        showDefaultNotification(pendingIntent, title, body, ACCEPTED_REQUEST_CHANNEL_ID, notificationId)
    }

    private fun handleNewEventNotification(bodyArgs: Array<String>,
                                           eventId: String) {
        val title = getString(R.string.new_event_notification_title)
        val body = getString(R.string.new_event_notification_body, bodyArgs[0], bodyArgs[1])
        val notificationId = eventId.hashCode() + NEW_EVENT_REQUEST_CODE
        val pendingIntent = createHomePendingIntent(NEW_EVENT_REQUEST_CODE, title, body)
        showDefaultNotification(pendingIntent, title, body, NEW_EVENT_CHANNEL_ID, notificationId)
    }

    private fun createHomePendingIntent(requestCode: Int,
                                        title: String,
                                        body: String): PendingIntent {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(LAUNCH_INFO, "$title: $body")
        return PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createEventDetailsPendingIntent(eventId: String,
                                                eventType: EventType,
                                                requestCode: Int): PendingIntent {
        val intent = Intent(this, EventDetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(EVENT_ID, eventId)
        intent.putExtra(EVENT_TYPE, eventType)
        return PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getEventType(evenType: String): EventType {
        return when (evenType) {
            EVENT_TYPE_PLAYER -> EventType.ACCEPTED
            EVENT_TYPE_ADMIN -> EventType.CREATED
            else -> EventType.ACCEPTED
        }
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
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val notificationsManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationsManager.notify(notificationId, notification)
    }
}