package com.boardly.analytics

import android.content.Context
import com.boardly.constants.EVENT_ADDED_EVENT
import com.boardly.constants.JOIN_REQUEST_ACCEPTED_EVENT
import com.boardly.constants.JOIN_REQUEST_SENT_EVENT
import com.boardly.constants.SIGN_UP_EVENT
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AnalyticsImpl @Inject constructor(context: Context) : Analytics {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logSignUpEvent() {
        firebaseAnalytics.logEvent(SIGN_UP_EVENT, null)
    }

    override fun logEventAddedEvent() {
        firebaseAnalytics.logEvent(EVENT_ADDED_EVENT, null)
    }

    override fun logJoinRequestSentEvent() {
        firebaseAnalytics.logEvent(JOIN_REQUEST_SENT_EVENT, null)
    }

    override fun logJoinRequestAcceptedEvent() {
        firebaseAnalytics.logEvent(JOIN_REQUEST_ACCEPTED_EVENT, null)
    }
}