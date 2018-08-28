package com.boardly.common.events

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.boardly.R
import com.boardly.common.events.models.Event
import com.boardly.constants.LEVEL_STRINGS_MAP
import com.boardly.extensions.formatForDisplay
import com.boardly.extensions.loadImageFromUrl
import java.util.*
import javax.inject.Inject


class EventUIRenderer @Inject constructor(private val activity: AppCompatActivity) {

    fun displayEventInfo(event: Event,
                         eventNameTextView: TextView,
                         gameTextView: TextView,
                         locationTextView: TextView,
                         boardGameImageView: ImageView,
                         seeDescriptionButton: Button,
                         levelTextView: TextView,
                         timeTextView: TextView,
                         mapButton: Button) {
        with(event) {
            eventNameTextView.text = eventName
            gameTextView.text = gameName
            locationTextView.text = placeName
            activity.loadImageFromUrl(boardGameImageView, gameImageUrl, R.drawable.board_game_placeholder)

            setSeeDescriptionButton(description, seeDescriptionButton)
            setLevelTextView(levelId, levelTextView)
            setDateTextView(timestamp, timeTextView)

            mapButton.setOnClickListener { openMap(placeLatitude, placeLongitude) }
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

    private fun setLevelTextView(levelId: String, levelTextView: TextView) {
        levelTextView.text = activity.getString(LEVEL_STRINGS_MAP[levelId]
                ?: com.boardly.R.string.empty)
    }

    private fun setDateTextView(timestamp: Long, timeTextView: TextView) {
        if (timestamp > 0) {
            timeTextView.text = Date(timestamp).formatForDisplay()
        } else {
            timeTextView.text = activity.getString(R.string.date_to_be_added)
        }
    }
}