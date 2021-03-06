package com.boardly.eventdetails.admin.list

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boardly.R
import com.boardly.common.players.list.PlayersDiffCallback
import com.boardly.common.players.models.Player
import com.boardly.eventdetails.admin.AdminFragment

class PendingPlayersAdapter(private val adminFragment: AdminFragment)
    : ListAdapter<Player, PendingPlayerViewHolder>(PlayersDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_pending_player, parent, false)
        return PendingPlayerViewHolder(itemView, adminFragment)
    }

    override fun onBindViewHolder(holder: PendingPlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}