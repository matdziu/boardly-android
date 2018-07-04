package com.boardly.pickgame.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.boardly.retrofit.gamesearch.models.SearchResult
import kotlinx.android.synthetic.main.item_search_result.view.gameNameTextView
import kotlinx.android.synthetic.main.item_search_result.view.yearPublishedTextView

class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(searchResult: SearchResult) {
        with(itemView) {
            gameNameTextView.text = searchResult.name
            yearPublishedTextView.text = searchResult.yearPublished
        }
    }
}