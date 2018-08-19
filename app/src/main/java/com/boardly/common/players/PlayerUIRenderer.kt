package com.boardly.common.players

import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.boardly.common.players.models.Player
import com.boardly.extensions.loadImageFromUrl
import com.google.firebase.auth.FirebaseAuth

class PlayerUIRenderer(private val activity: AppCompatActivity) {

    fun displayPlayerInfo(player: Player,
                          playerImageView: ImageView,
                          nameTextView: TextView,
                          helloTextView: TextView,
                          ratingTextView: TextView,
                          ratingImageView: ImageView,
                          rateButton: Button? = null) {
        activity.loadImageFromUrl(playerImageView, player.profilePicture, com.boardly.R.drawable.profile_picture_shape)
        nameTextView.text = player.name
        helloTextView.text = player.helloText
        displayRating(player.rating, ratingTextView, ratingImageView)
        displayRateButton(player.id, rateButton)
    }

    private fun displayRateButton(playerId: String, rateButton: Button?) {
        rateButton?.let {
            if (playerId == FirebaseAuth.getInstance().uid) it.visibility = View.GONE
            else it.visibility = View.VISIBLE
        }
    }

    private fun displayRating(rating: Double?, ratingTextView: TextView, ratingImageView: ImageView) {
        if (rating != null) {
            ratingTextView.visibility = View.VISIBLE
            ratingImageView.visibility = View.VISIBLE
            ratingTextView.text = rating.toString()
        } else {
            ratingTextView.visibility = View.GONE
            ratingImageView.visibility = View.GONE
        }
    }
}