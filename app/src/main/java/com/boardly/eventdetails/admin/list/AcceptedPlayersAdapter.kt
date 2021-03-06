package com.boardly.eventdetails.admin.list

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boardly.R
import com.boardly.common.players.list.PlayersDiffCallback
import com.boardly.common.players.models.Player
import com.boardly.eventdetails.admin.AdminFragment

class AcceptedPlayersAdapter(private val adminFragment: AdminFragment)
    : ListAdapter<Player, AcceptedPlayerViewHolder>(PlayersDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptedPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_accepted_player, parent, false)
        return AcceptedPlayerViewHolder(itemView, adminFragment)
    }

    override fun onBindViewHolder(holder: AcceptedPlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}