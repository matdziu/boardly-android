package com.boardly.discover.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.boardly.R
import com.boardly.common.events.models.Event
import com.boardly.discover.models.Place
import com.boardly.event.EventActivity
import com.boardly.extensions.loadImageFromUrl
import com.boardly.extensions.setOnClickListener
import com.boardly.gamescollection.GamesCollectionActivity


class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(place: Place) = with(itemView) {
        val placeNameTextView = this.findViewById<TextView>(R.id.placeNameTextView)
        val descriptionTextView = this.findViewById<TextView>(R.id.descriptionTextView)
        val placeImageView = this.findViewById<ImageView>(R.id.placeImageView)
        val locationTextView = this.findViewById<TextView>(R.id.locationTextView)
        val locationImageView = this.findViewById<ImageView>(R.id.locationImageView)
        val callImageView = this.findViewById<ImageView>(R.id.callImageView)
        val callButton = this.findViewById<Button>(R.id.callButton)
        val createEventButton = this.findViewById<Button>(R.id.createEventButton)
        val createEventImageView = this.findViewById<ImageView>(R.id.createEventImageView)
        val viewBoardGamesImageView = this.findViewById<ImageView>(R.id.viewBoardGamesImageView)
        val viewBoardGamesButton = this.findViewById<Button>(R.id.viewBoardGamesButton)

        placeNameTextView.text = place.name
        descriptionTextView.text = place.description
        context.loadImageFromUrl(placeImageView, place.imageUrl, R.drawable.place_placeholder)
        locationTextView.text = place.locationName

        listOf(locationImageView, locationTextView).setOnClickListener {
            openMap(place.placeLatitude, place.placeLongitude, context)
        }
        listOf(callImageView, callButton).setOnClickListener {
            openPhone(place.phoneNumber, context)
        }
        listOf(createEventButton, createEventImageView).setOnClickListener {
            EventActivity.startAddMode(
                context, Event(
                    placeName = place.locationName,
                    placeLatitude = place.placeLatitude,
                    placeLongitude = place.placeLongitude
                )
            )
        }
        listOf(viewBoardGamesImageView, viewBoardGamesButton).setOnClickListener {
            GamesCollectionActivity.startViewMode(context, place.collectionId)
        }
        placeImageView.setOnClickListener { openBoardGameInfoPage(place.pageLink, context) }
    }

    private fun openMap(latitude: Double, longitude: Double, context: Context) {
        with(context) {
            val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            }
        }
    }

    private fun openBoardGameInfoPage(link: String, context: Context) {
        if (link.trim().isNotEmpty()) {
            with(context) {
                val infoPageIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                try {
                    startActivity(infoPageIntent)
                } catch (e: Exception) {
                    Toast.makeText(this, R.string.no_link_found, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openPhone(phoneNumber: String, context: Context) {
        val number = Uri.parse("tel:$phoneNumber")
        val callIntent = Intent(Intent.ACTION_DIAL, number)
        context.startActivity(callIntent)
    }
}