package com.boardly.analytics

import android.content.Context
import android.os.Bundle
import com.boardly.constants.EVENT_ADDED_EVENT
import com.boardly.constants.GAME_ID_PARAM
import com.boardly.constants.JOIN_REQUEST_ACCEPTED_EVENT
import com.boardly.constants.JOIN_REQUEST_SENT_EVENT
import com.boardly.constants.PLACE_LATITUDE_PARAM
import com.boardly.constants.PLACE_LONGITUDE_PARAM
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AnalyticsImpl @Inject constructor(context: Context) : Analytics {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEventAddedEvent(gameId: String, placeLatitude: Double, placeLongitude: Double) {
        val params = Bundle()
        params.putString(GAME_ID_PARAM, gameId)
        params.putDouble(PLACE_LATITUDE_PARAM, placeLatitude)
        params.putDouble(PLACE_LONGITUDE_PARAM, placeLongitude)
        firebaseAnalytics.logEvent(EVENT_ADDED_EVENT, params)
    }

    override fun logJoinRequestSentEvent() {
        firebaseAnalytics.logEvent(JOIN_REQUEST_SENT_EVENT, null)
    }

    override fun logJoinRequestAcceptedEvent() {
        firebaseAnalytics.logEvent(JOIN_REQUEST_ACCEPTED_EVENT, null)
    }
}