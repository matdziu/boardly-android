package com.boardly.eventdetails.players.list

import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.boardly.R
import com.boardly.common.events.models.Player
import com.boardly.injection.modules.GlideApp
import kotlinx.android.synthetic.main.item_player.view.helloTextView
import kotlinx.android.synthetic.main.item_player.view.nameTextView
import kotlinx.android.synthetic.main.item_player.view.playerImageView

class AcceptedPlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(player: Player) {
        with(itemView) {
            loadImageFromUrl(playerImageView, player.profilePicture, R.drawable.profile_picture_shape)
            nameTextView.text = player.name
            helloTextView.text = player.helloText
        }
    }

    private fun loadImageFromUrl(imageView: ImageView, pictureUrl: String, @DrawableRes placeholderId: Int) {
        GlideApp.with(itemView.context)
                .load(pictureUrl)
                .placeholder(placeholderId)
                .into(imageView)
    }
}