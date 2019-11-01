package com.boardly.common.search

import android.support.v7.util.DiffUtil

class SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResultData>() {

    override fun areItemsTheSame(oldItem: SearchResultData, newItem: SearchResultData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchResultData, newItem: SearchResultData): Boolean {
        return oldItem == newItem
    }
}