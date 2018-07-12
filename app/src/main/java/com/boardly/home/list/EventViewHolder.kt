package com.boardly.home.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.boardly.R
import com.boardly.constants.LEVEL_STRINGS_MAP
import com.boardly.extensions.formatForDisplay
import com.boardly.extensions.formatForMaxOf
import com.boardly.home.HomeActivity
import com.boardly.home.models.Event
import kotlinx.android.synthetic.main.item_event.view.boardGameImageView
import kotlinx.android.synthetic.main.item_event.view.eventNameTextView
import kotlinx.android.synthetic.main.item_event.view.gameTextView
import kotlinx.android.synthetic.main.item_event.view.levelTextView
import kotlinx.android.synthetic.main.item_event.view.locationTextView
import kotlinx.android.synthetic.main.item_event.view.numberOfPlayersTextView
import kotlinx.android.synthetic.main.item_event.view.seeDescriptionButton
import kotlinx.android.synthetic.main.item_event.view.timeTextView
import java.util.*

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val parentActivity = itemView.context as HomeActivity

    fun bind(event: Event) {
        with(itemView) {
            eventNameTextView.text = event.eventName
            gameTextView.text = event.gameName
            levelTextView.text = context.getString(LEVEL_STRINGS_MAP[event.levelId]
                    ?: R.string.empty)
            locationTextView.text = event.placeName
            timeTextView.text = Date(event.timestamp).formatForDisplay()
            numberOfPlayersTextView.text = event.currentNumberOfPlayers.toString().formatForMaxOf(event.maxPlayers.toString())
            parentActivity.loadImageFromUrl(boardGameImageView, event.gameImageUrl, R.drawable.board_game_placeholder)
            seeDescriptionButton.setOnClickListener { launchDescriptionDialog(event.description) }
        }
    }

    private fun launchDescriptionDialog(description: String) {
        android.app.AlertDialog.Builder(parentActivity)
                .setMessage(description)
                .setTitle(R.string.description_text)
                .setPositiveButton(R.string.close_dialog) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
    }
}