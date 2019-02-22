package com.boardly.discover.list

import android.support.v7.util.DiffUtil
import com.boardly.discover.models.Place

class PlaceDiffCallback : DiffUtil.ItemCallback<Place>() {

    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }
}