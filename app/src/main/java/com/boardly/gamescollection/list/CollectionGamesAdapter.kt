package com.boardly.gamescollection.list

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.boardly.R
import com.boardly.gamescollection.models.CollectionGame

class CollectionGamesAdapter : ListAdapter<CollectionGame, CollectionGameViewHolder>(CollectionGameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionGameViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_collection_game, parent, false)
        return CollectionGameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CollectionGameViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}