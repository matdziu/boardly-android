package com.boardly.eventdetails.players.list

import android.support.v7.util.DiffUtil
import com.boardly.common.events.models.Player

class PlayersDiffCallback : DiffUtil.ItemCallback<Player>() {

    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }
}