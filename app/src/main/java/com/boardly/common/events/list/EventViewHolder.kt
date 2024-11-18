package com.boardly.common.events.list

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.base.joinevent.BaseJoinEventActivity
import com.boardly.common.events.EventUIRenderer
import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.customviews.BoardlyEditText
import com.boardly.eventdetails.EventDetailsActivity
import com.boardly.extensions.setOnClickListener
import com.boardly.home.JoinDialogValidator
import com.boardly.home.models.JoinEventData

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val parentActivity = itemView.context as BaseActivity
    private val eventUIRenderer = EventUIRenderer(parentActivity)

    fun bind(event: Event) {
        with(itemView) {
            val eventNameTextView = this.findViewById<TextView>(R.id.eventNameTextView)
            val gameTextView = this.findViewById<TextView>(R.id.gameTextView)
            val locationTextView = this.findViewById<TextView>(R.id.locationTextView)
            val locationImageView = this.findViewById<ImageView>(R.id.locationImageView)
            val boardGameImageView = this.findViewById<ImageView>(R.id.boardGameImageView)
            val seeDescriptionButton = this.findViewById<Button>(R.id.seeDescriptionButton)
            val timeTextView = this.findViewById<TextView>(R.id.timeTextView)
            val timeImageView = this.findViewById<ImageView>(R.id.timeImageView)
            val gameTextView2 = this.findViewById<TextView>(R.id.gameTextView2)
            val boardGameImageView2 = this.findViewById<ImageView>(R.id.boardGameImageView2)
            val gameTextView3 = this.findViewById<TextView>(R.id.gameTextView3)
            val boardGameImageView3 = this.findViewById<ImageView>(R.id.boardGameImageView3)
            eventUIRenderer.displayEventInfo(
                event,
                eventNameTextView,
                gameTextView,
                locationTextView,
                locationImageView,
                boardGameImageView,
                seeDescriptionButton,
                timeTextView,
                timeImageView,
                gameTextView2,
                boardGameImageView2,
                gameTextView3,
                boardGameImageView3
            )
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
            val openEventScreenButton = this.findViewById<Button>(R.id.openEventScreenButton)
            val gameTextView = this.findViewById<TextView>(R.id.gameTextView)
            val gameTextView2 = this.findViewById<TextView>(R.id.gameTextView2)
            val gameTextView3 = this.findViewById<TextView>(R.id.gameTextView3)
            val eventNameTextView = this.findViewById<TextView>(R.id.eventNameTextView)
            val joinEventButton = this.findViewById<Button>(R.id.joinEventButton)
            openEventScreenButton.visibility = View.GONE
            listOf(joinEventButton, gameTextView, gameTextView2, gameTextView3, eventNameTextView)
                .setOnClickListener { launchHelloDialog(eventId) }
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
                val helloEditText = this.findViewById<BoardlyEditText>(R.id.helloEditText)
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
        val homeActivity = parentActivity as BaseJoinEventActivity
        homeActivity.joinEventSubject.onNext(joinEventData)
    }

    private fun setCreatedClickAction(event: Event) {
        with(itemView) {
            val openEventScreenButton = this.findViewById<Button>(R.id.openEventScreenButton)
            val gameTextView = this.findViewById<TextView>(R.id.gameTextView)
            val gameTextView2 = this.findViewById<TextView>(R.id.gameTextView2)
            val gameTextView3 = this.findViewById<TextView>(R.id.gameTextView3)
            val eventNameTextView = this.findViewById<TextView>(R.id.eventNameTextView)
            openEventScreenButton.visibility = View.VISIBLE
            listOf(
                openEventScreenButton,
                gameTextView,
                gameTextView2,
                gameTextView3,
                eventNameTextView
            )
                .setOnClickListener {
                    EventDetailsActivity.start(
                        parentActivity,
                        event.eventId,
                        event.type
                    )
                }
        }
    }

    private fun setPendingClickAction() {
        with(itemView) {
            val openEventScreenButton = this.findViewById<Button>(R.id.openEventScreenButton)
            openEventScreenButton.visibility = View.GONE
        }
    }

    private fun setAcceptedClickAction(event: Event) {
        with(itemView) {
            val openEventScreenButton = this.findViewById<Button>(R.id.openEventScreenButton)
            val gameTextView = this.findViewById<TextView>(R.id.gameTextView)
            val gameTextView2 = this.findViewById<TextView>(R.id.gameTextView2)
            val gameTextView3 = this.findViewById<TextView>(R.id.gameTextView3)
            val eventNameTextView = this.findViewById<TextView>(R.id.eventNameTextView)
            openEventScreenButton.visibility = View.VISIBLE
            listOf(
                openEventScreenButton,
                gameTextView,
                gameTextView2,
                gameTextView3,
                eventNameTextView
            )
                .setOnClickListener {
                    EventDetailsActivity.start(
                        parentActivity,
                        event.eventId,
                        event.type
                    )
                }
        }
    }

    private fun setTypeLabel(type: EventType) {
        with(itemView) {
            val joinEventButton = this.findViewById<Button>(R.id.joinEventButton)
            val createdTextView = this.findViewById<TextView>(R.id.createdTextView)
            val pendingTextView = this.findViewById<TextView>(R.id.pendingTextView)
            val acceptedTextView = this.findViewById<TextView>(R.id.acceptedTextView)
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
            val joinEventButton = this.findViewById<Button>(R.id.joinEventButton)
            val createdTextView = this.findViewById<TextView>(R.id.createdTextView)
            val pendingTextView = this.findViewById<TextView>(R.id.pendingTextView)
            val acceptedTextView = this.findViewById<TextView>(R.id.acceptedTextView)
            val viewsList =
                listOf(joinEventButton, acceptedTextView, pendingTextView, createdTextView)
            for (view in viewsList) {
                if (selectedView == view) view.visibility = View.VISIBLE
                else view.visibility = View.INVISIBLE
            }
        }
    }
}