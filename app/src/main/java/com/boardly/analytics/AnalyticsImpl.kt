package com.boardly.analytics

import android.content.Context
import android.os.Bundle
import com.boardly.constants.EVENT_ADDED_EVENT
import com.boardly.constants.GAME_ID_PARAM
import com.boardly.constants.JOIN_REQUEST_ACCEPTED_EVENT
import com.boardly.constants.JOIN_REQUEST_SENT_EVENT
import com.boardly.constants.LAT_LONG_PARAM
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AnalyticsImpl @Inject constructor(context: Context) : Analytics {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEventAddedEvent(gameId: String,
                                    gameId2: String,
                                    gameId3: String,
                                    placeLatitude: Double,
                                    placeLongitude: Double) {
        val params = Bundle()
        params.putString(GAME_ID_PARAM, "$gameId${appendWithAmpersand(gameId2)}${appendWithAmpersand(gameId3)}")
        params.putString(LAT_LONG_PARAM, "$placeLatitude&$placeLongitude")
        firebaseAnalytics.logEvent(EVENT_ADDED_EVENT, params)
    }

    private fun appendWithAmpersand(gameId: String): String {
        return if (gameId.isNotEmpty()) "&$gameId" else ""
    }

    override fun logJoinRequestSentEvent() {
        firebaseAnalytics.logEvent(JOIN_REQUEST_SENT_EVENT, null)
    }

    override fun logJoinRequestAcceptedEvent() {
        firebaseAnalytics.logEvent(JOIN_REQUEST_ACCEPTED_EVENT, null)
    }
}