package com.boardly.pickgame.list

import android.support.v7.util.DiffUtil
import com.boardly.retrofit.gameservice.models.SearchResult

class SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResult>() {

    override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem == newItem
    }
}