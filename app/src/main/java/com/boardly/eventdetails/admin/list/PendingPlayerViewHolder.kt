package com.boardly.eventdetails.admin.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.boardly.base.BaseActivity
import com.boardly.common.players.PlayerUIRenderer
import com.boardly.common.players.models.Player
import com.boardly.eventdetails.admin.AdminFragment
import kotlinx.android.synthetic.main.item_admin_pending_player.view.acceptButton
import kotlinx.android.synthetic.main.item_admin_pending_player.view.helloTextView
import kotlinx.android.synthetic.main.layout_player.view.nameTextView
import kotlinx.android.synthetic.main.layout_player.view.playerImageView
import kotlinx.android.synthetic.main.layout_player.view.ratingImageView
import kotlinx.android.synthetic.main.layout_player.view.ratingTextView

class PendingPlayerViewHolder(itemView: View,
                              private val adminFragment: AdminFragment) : RecyclerView.ViewHolder(itemView) {

    private val parentActivity = itemView.context as BaseActivity
    private val playerUIRenderer = PlayerUIRenderer(parentActivity)

    fun bind(player: Player) {
        with(itemView) {
            playerUIRenderer.displayPlayerInfo(player,
                    playerImageView,
                    nameTextView,
                    helloTextView,
                    ratingTextView,
                    ratingImageView)
            acceptButton.setOnClickListener { adminFragment.acceptPlayerSubject.onNext(player.id) }
        }
    }
}