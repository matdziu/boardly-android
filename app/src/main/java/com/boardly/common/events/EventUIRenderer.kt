package com.boardly.common.events

import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.boardly.R
import com.boardly.common.events.models.Event
import com.boardly.constants.RPG_TYPE
import com.boardly.extensions.HOUR_IN_MILLIS
import com.boardly.extensions.clearFromType
import com.boardly.extensions.formatForDisplay
import com.boardly.extensions.isOfType
import com.boardly.extensions.loadImageFromUrl
import com.boardly.extensions.setOnClickListener
import java.util.Date
import javax.inject.Inject

class EventUIRenderer @Inject constructor(private val activity: AppCompatActivity) {

    fun displayEventInfo(event: Event,
                         eventNameTextView: TextView,
                         gameTextView: TextView,
                         locationTextView: TextView,
                         locationImageView: ImageView,
                         boardGameImageView: ImageView,
                         seeDescriptionButton: Button,
                         timeTextView: TextView,
                         timeImageView: ImageView,
                         gameTextView2: TextView,
                         boardGameImageView2: ImageView,
                         gameTextView3: TextView,
                         boardGameImageView3: ImageView) {
        with(event) {
            eventNameTextView.text = eventName
            locationTextView.text = placeName

            displayGameNameAndImage(gameName, gameTextView, gameImageUrl, boardGameImageView)
            displayGameNameAndImage(gameName2, gameTextView2, gameImageUrl2, boardGameImageView2)
            displayGameNameAndImage(gameName3, gameTextView3, gameImageUrl3, boardGameImageView3)

            setSeeDescriptionButton(description, seeDescriptionButton)
            setDateTextView(timestamp, timeTextView)

            listOf(locationTextView, locationImageView).setOnClickListener { openMap(placeLatitude, placeLongitude) }
            listOf(timeTextView, timeImageView).setOnClickListener {
                openCalendar(eventName,
                        gameName,
                        gameName2,
                        gameName3,
                        timestamp,
                        placeName)
            }
//            boardGameImageView.setOnClickListener { openBoardGameInfoPage(gameId) }
//            boardGameImageView2.setOnClickListener { openBoardGameInfoPage(gameId2) }
//            boardGameImageView3.setOnClickListener { openBoardGameInfoPage(gameId3) }
        }
    }

    private fun displayGameNameAndImage(gameName: String, gameTextView: TextView,
                                        gameImageUrl: String, gameImageView: ImageView) {
        if (gameName.isNotEmpty()) {
            gameTextView.visibility = View.VISIBLE
            gameImageView.visibility = View.VISIBLE
            gameTextView.text = gameName
            activity.loadImageFromUrl(gameImageView, gameImageUrl, R.drawable.board_game_placeholder)
        } else {
            gameTextView.visibility = View.GONE
            gameImageView.visibility = View.GONE
        }
    }

    private fun openMap(latitude: Double, longitude: Double) {
        with(activity) {
            val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            }
        }
    }

    private fun openBoardGameInfoPage(gameId: String) {
        with(activity) {
            val endpoint = if (gameId.isOfType(RPG_TYPE)) "rpg/${gameId.clearFromType(RPG_TYPE)}" else "boardgame/$gameId"
            val infoPageIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://boardgamegeek.com/$endpoint"))
            startActivity(infoPageIntent)
        }
    }

    private fun openCalendar(eventName: String,
                             gameName: String,
                             gameName2: String,
                             gameName3: String,
                             startTime: Long,
                             placeName: String) {
        if (startTime > 0) {
            val eventTitle = activity.getString(R.string.calendar_event_title, eventName)
            val eventDescription = activity.getString(R.string.calendar_event_description,
                    "$gameName${appendWithComma(gameName2)}${appendWithComma(gameName3)}.")
            val intent = Intent(Intent.ACTION_INSERT)
                    .setData(Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTime + HOUR_IN_MILLIS)
                    .putExtra(Events.TITLE, eventTitle)
                    .putExtra(Events.DESCRIPTION, eventDescription)
                    .putExtra(Events.EVENT_LOCATION, placeName)
            activity.startActivity(intent)
        }
    }

    private fun appendWithComma(gameName: String): String {
        return if (gameName.isNotEmpty()) ", $gameName" else ""
    }

    private fun setSeeDescriptionButton(description: String, seeDescriptionButton: Button) {
        if (description.isNotEmpty()) {
            seeDescriptionButton.visibility = View.VISIBLE
            seeDescriptionButton.setOnClickListener { launchDescriptionDialog(description) }
        } else {
            seeDescriptionButton.visibility = View.GONE
        }
    }

    private fun launchDescriptionDialog(description: String) {
        android.app.AlertDialog.Builder(activity)
                .setMessage(description)
                .setTitle(R.string.description_text)
                .setPositiveButton(R.string.close_dialog) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
    }

    private fun setDateTextView(timestamp: Long, timeTextView: TextView) {
        if (timestamp > 0) {
            timeTextView.text = Date(timestamp).formatForDisplay()
        } else {
            timeTextView.text = activity.getString(R.string.date_to_be_added)
        }
    }
}