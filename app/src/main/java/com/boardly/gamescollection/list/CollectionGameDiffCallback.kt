package com.boardly.gamescollection.list

import android.support.v7.util.DiffUtil
import com.boardly.gamescollection.models.CollectionGame

class CollectionGameDiffCallback : DiffUtil.ItemCallback<CollectionGame>() {

    override fun areItemsTheSame(oldItem: CollectionGame, newItem: CollectionGame): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CollectionGame, newItem: CollectionGame): Boolean {
        return oldItem == newItem
    }
}