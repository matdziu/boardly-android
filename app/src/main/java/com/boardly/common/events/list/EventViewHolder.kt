package com.boardly.common.events.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.constants.LEVEL_STRINGS_MAP
import com.boardly.extensions.formatForDisplay
import com.boardly.extensions.formatForMaxOf
import com.boardly.home.HomeActivity
import kotlinx.android.synthetic.main.item_event.view.acceptedTextView
import kotlinx.android.synthetic.main.item_event.view.boardGameImageView
import kotlinx.android.synthetic.main.item_event.view.createdTextView
import kotlinx.android.synthetic.main.item_event.view.eventNameTextView
import kotlinx.android.synthetic.main.item_event.view.gameTextView
import kotlinx.android.synthetic.main.item_event.view.joinEventButton
import kotlinx.android.synthetic.main.item_event.view.levelTextView
import kotlinx.android.synthetic.main.item_event.view.locationTextView
import kotlinx.android.synthetic.main.item_event.view.numberOfPlayersTextView
import kotlinx.android.synthetic.main.item_event.view.pendingTextView
import kotlinx.android.synthetic.main.item_event.view.seeDescriptionButton
import kotlinx.android.synthetic.main.item_event.view.timeTextView
import java.util.*

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val parentActivity = itemView.context as BaseActivity

    fun bind(event: Event) {
        with(itemView) {
            eventNameTextView.text = event.eventName
            gameTextView.text = event.gameName
            locationTextView.text = event.placeName
            numberOfPlayersTextView.text = event.currentNumberOfPlayers.toString().formatForMaxOf(event.maxPlayers.toString())
            parentActivity.loadImageFromUrl(boardGameImageView, event.gameImageUrl, R.drawable.board_game_placeholder)

            setSeeDescriptionButton(event.description)
            setLevelTextView(event.levelId)
            setDateTextView(event.timestamp)
            setTypeLabel(event.type)
            setClickAction(event)
        }
    }

    private fun setClickAction(event: Event) {
        when (event.type) {
            EventType.DEFAULT -> setDefaultClickAction(event)
            EventType.CREATED -> setCreatedClickAction()
            EventType.PENDING -> setPendingClickAction()
            EventType.ACCEPTED -> setAcceptedClickAction()
        }
    }

    private fun setDefaultClickAction(event: Event) {
        val homeActivity = parentActivity as HomeActivity
        itemView.joinEventButton.setOnClickListener { homeActivity.joinEventSubject.onNext(event.eventId) }
    }

    private fun setCreatedClickAction() {

    }

    private fun setPendingClickAction() {

    }

    private fun setAcceptedClickAction() {

    }

    private fun launchDescriptionDialog(description: String) {
        android.app.AlertDialog.Builder(parentActivity)
                .setMessage(description)
                .setTitle(R.string.description_text)
                .setPositiveButton(R.string.close_dialog) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
    }

    private fun setSeeDescriptionButton(description: String) {
        with(itemView) {
            if (description.isNotEmpty()) {
                seeDescriptionButton.visibility = View.VISIBLE
                seeDescriptionButton.setOnClickListener { launchDescriptionDialog(description) }
            } else {
                seeDescriptionButton.visibility = View.GONE
            }
        }
    }

    private fun setLevelTextView(levelId: String) {
        with(itemView) {
            levelTextView.text = context.getString(LEVEL_STRINGS_MAP[levelId] ?: R.string.empty)
        }
    }

    private fun setDateTextView(timestamp: Long) {
        with(itemView) {
            if (timestamp > 0) {
                timeTextView.text = Date(timestamp).formatForDisplay()
            } else {
                timeTextView.text = context.getString(R.string.date_to_be_added)
            }
        }
    }

    private fun setTypeLabel(type: EventType) {
        with(itemView) {
            when (type) {
                EventType.DEFAULT -> makeVisibleOnly(joinEventButton)
                EventType.CREATED -> makeVisibleOnly(createdTextView)
                EventType.PENDING -> makeVisibleOnly(pendingTextView)
                EventType.ACCEPTED -> makeVisibleOnly(acceptedTextView)
            }
        }
    }

    private fun makeVisibleOnly(selectedView: View) {
        with(itemView) {
            val viewsList = listOf(joinEventButton, acceptedTextView, pendingTextView, createdTextView)
            for (view in viewsList) {
                if (selectedView == view) view.visibility = View.VISIBLE
                else view.visibility = View.INVISIBLE
            }
        }
    }
}