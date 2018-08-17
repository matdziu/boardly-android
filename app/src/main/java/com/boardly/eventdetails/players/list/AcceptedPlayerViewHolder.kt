package com.boardly.eventdetails.players.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.boardly.R
import com.boardly.common.events.models.Player
import com.boardly.extensions.loadImageFromUrl
import kotlinx.android.synthetic.main.item_player.view.helloTextView
import kotlinx.android.synthetic.main.item_player.view.nameTextView
import kotlinx.android.synthetic.main.item_player.view.playerImageView
import kotlinx.android.synthetic.main.item_player.view.ratingImageView
import kotlinx.android.synthetic.main.item_player.view.ratingTextView

class AcceptedPlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(player: Player) {
        with(itemView) {
            context.loadImageFromUrl(playerImageView, player.profilePicture, R.drawable.profile_picture_shape)
            nameTextView.text = player.name
            helloTextView.text = player.helloText
            displayRating(player.rating)
        }
    }

    private fun displayRating(rating: Double?) {
        with(itemView) {
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
}