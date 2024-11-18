package com.boardly.eventdetails.admin.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.players.RatingPlayerUIRenderer
import com.boardly.common.players.models.Player
import com.boardly.eventdetails.admin.AdminFragment

class AcceptedPlayerViewHolder(
    itemView: View, private val adminFragment: AdminFragment
) : RecyclerView.ViewHolder(itemView) {

    private val parentActivity = itemView.context as BaseActivity
    private val playerUIRenderer = RatingPlayerUIRenderer(parentActivity)

    fun bind(player: Player) {
        with(itemView) {
            val playerImageView = this.findViewById<ImageView>(R.id.playerImageView)
            val nameTextView = this.findViewById<TextView>(R.id.nameTextView)
            val helloTextView = this.findViewById<TextView>(R.id.helloTextView)
            val ratingTextView = this.findViewById<TextView>(R.id.ratingTextView)
            val ratingImageView = this.findViewById<ImageView>(R.id.ratingImageView)
            val rateButton = this.findViewById<Button>(R.id.rateButton)
            val kickButton = this.findViewById<Button>(R.id.kickButton)
            playerUIRenderer.displayPlayerInfo(
                player,
                playerImageView,
                nameTextView,
                helloTextView,
                ratingTextView,
                ratingImageView,
                rateButton,
                adminFragment
            )
            kickButton.setOnClickListener { adminFragment.kickPlayerSubject.onNext(player.id) }
        }
    }
}