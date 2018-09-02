package com.boardly.common.events.list

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.events.EventUIRenderer
import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.eventdetails.EventDetailsActivity
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
import kotlinx.android.synthetic.main.item_event.view.locationImageView
import kotlinx.android.synthetic.main.item_event.view.locationTextView
import kotlinx.android.synthetic.main.item_event.view.openEventScreenButton
import kotlinx.android.synthetic.main.item_event.view.pendingTextView
import kotlinx.android.synthetic.main.item_event.view.seeDescriptionButton
import kotlinx.android.synthetic.main.item_event.view.timeTextView
import kotlinx.android.synthetic.main.view_hello_dialog.view.helloEditText

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val parentActivity = itemView.context as BaseActivity
    private val eventUIRenderer = EventUIRenderer(parentActivity)

    fun bind(event: Event) {
        with(itemView) {
            eventUIRenderer.displayEventInfo(event,
                    eventNameTextView,
                    gameTextView,
                    locationTextView,
                    locationImageView,
                    boardGameImageView,
                    seeDescriptionButton,
                    levelTextView,
                    timeTextView)
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
        with(itemView) {
            openEventScreenButton.visibility = View.GONE
            joinEventButton.setOnClickListener { launchHelloDialog(eventId) }
        }
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
        with(itemView) {
            openEventScreenButton.visibility = View.VISIBLE
            openEventScreenButton.setOnClickListener { EventDetailsActivity.start(parentActivity, event.eventId, event.type) }
        }
    }

    private fun setPendingClickAction() {
        with(itemView) {
            openEventScreenButton.visibility = View.GONE
        }
    }

    private fun setAcceptedClickAction(event: Event) {
        with(itemView) {
            openEventScreenButton.visibility = View.VISIBLE
            openEventScreenButton.setOnClickListener { EventDetailsActivity.start(parentActivity, event.eventId, event.type) }
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