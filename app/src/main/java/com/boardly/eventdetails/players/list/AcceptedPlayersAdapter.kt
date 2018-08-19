package com.boardly.eventdetails.players.list

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boardly.R
import com.boardly.common.players.list.PlayersDiffCallback
import com.boardly.common.players.models.Player
import com.boardly.eventdetails.players.PlayersFragment

class AcceptedPlayersAdapter(private val playersFragment: PlayersFragment)
    : ListAdapter<Player, AcceptedPlayerViewHolder>(PlayersDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptedPlayerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return AcceptedPlayerViewHolder(itemView, playersFragment)
    }

    override fun onBindViewHolder(holder: AcceptedPlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}