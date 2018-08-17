package com.boardly.common.events.list

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.constants.LEVEL_STRINGS_MAP
import com.boardly.eventdetails.EventDetailsActivity
import com.boardly.extensions.formatForDisplay
import com.boardly.extensions.formatForMaxOf
import com.boardly.extensions.loadImageFromUrl
import com.boardly.home.HomeActivity
import com.boardly.home.JoinDialogValidator
import com.boardly.home.models.JoinEventData
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
import kotlinx.android.synthetic.main.view_hello_dialog.view.helloEditText
import java.util.*

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val parentActivity = itemView.context as BaseActivity

    fun bind(event: Event) {
        with(itemView) {
            eventNameTextView.text = event.eventName
            gameTextView.text = event.gameName
            locationTextView.text = event.placeName
            numberOfPlayersTextView.text = event.currentNumberOfPlayers.toString().formatForMaxOf(event.maxPlayers.toString())
            context.loadImageFromUrl(boardGameImageView, event.gameImageUrl, R.drawable.board_game_placeholder)

            setSeeDescriptionButton(event.description)
            setLevelTextView(event.levelId)
            setDateTextView(event.timestamp)
            setTypeLabel(event.type)
            setClickAction(event)
        }
    }

    private fun setClickAction(event: Event) {
        when (event.type) {
            EventType.DEFAULT -> setDefaultClickAction(event.eventId)
            EventType.CREATED -> setCreatedClickAction(event)
            EventType.PENDING -> setPendingClickAction()
            EventType.ACCEPTED -> setAcceptedClickAction(event)
        }
    }

    private fun setDefaultClickAction(eventId: String) {
        itemView.joinEventButton.setOnClickListener { launchHelloDialog(eventId) }
    }

    private fun launchHelloDialog(eventId: String) {
        val dialogView = LayoutInflater.from(itemView.context)
                .inflate(R.layout.view_hello_dialog, itemView.parent as ViewGroup, false)

        val joinDialogValidator = JoinDialogValidator()

        val dialog = android.app.AlertDialog.Builder(parentActivity)
                .setTitle(R.string.hello_dialog_title)
                .setPositiveButton(R.string.hello_dialog_positive_text, null)
                .setNegativeButton(R.string.hello_dialog_negative_text, null)
                .setView(dialogView)
                .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            with(dialogView) {
                val helloText = helloEditText.text.toString()
                if (joinDialogValidator.validate(helloText)) {
                    emitJoinEventData(JoinEventData(eventId, helloText))
                    dialog.cancel()
                } else {
                    helloEditText.showError(true)
                }
            }

        }
    }

    private fun emitJoinEventData(joinEventData: JoinEventData) {
        val homeActivity = parentActivity as HomeActivity
        homeActivity.joinEventSubject.onNext(joinEventData)
    }

    private fun setCreatedClickAction(event: Event) {
        itemView.setOnClickListener { EventDetailsActivity.start(parentActivity, event) }
    }

    private fun setPendingClickAction() {

    }

    private fun setAcceptedClickAction(event: Event) {
        itemView.setOnClickListener { EventDetailsActivity.start(parentActivity, event) }
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