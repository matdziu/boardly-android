package com.boardly.discover.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import com.boardly.R
import com.boardly.common.events.models.Event
import com.boardly.discover.models.Place
import com.boardly.event.EventActivity
import com.boardly.extensions.loadImageFromUrl
import com.boardly.extensions.setOnClickListener
import com.boardly.gamescollection.GamesCollectionActivity
import kotlinx.android.synthetic.main.item_place.view.callButton
import kotlinx.android.synthetic.main.item_place.view.callImageView
import kotlinx.android.synthetic.main.item_place.view.createEventButton
import kotlinx.android.synthetic.main.item_place.view.createEventImageView
import kotlinx.android.synthetic.main.item_place.view.descriptionTextView
import kotlinx.android.synthetic.main.item_place.view.locationImageView
import kotlinx.android.synthetic.main.item_place.view.locationTextView
import kotlinx.android.synthetic.main.item_place.view.placeImageView
import kotlinx.android.synthetic.main.item_place.view.placeNameTextView
import kotlinx.android.synthetic.main.item_place.view.viewBoardGamesButton
import kotlinx.android.synthetic.main.item_place.view.viewBoardGamesImageView


class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(place: Place) = with(itemView) {
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
            EventActivity.startAddMode(context, Event(
                    placeName = place.locationName,
                    placeLatitude = place.placeLatitude,
                    placeLongitude = place.placeLongitude
            ))
        }
        listOf(viewBoardGamesImageView, viewBoardGamesButton).setOnClickListener {
            GamesCollectionActivity.start(context, place.collectionId)
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
        with(context) {
            val infoPageIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(infoPageIntent)
        }
    }

    private fun openPhone(phoneNumber: String, context: Context) {
        val number = Uri.parse("tel:$phoneNumber")
        val callIntent = Intent(Intent.ACTION_DIAL, number)
        context.startActivity(callIntent)
    }
}