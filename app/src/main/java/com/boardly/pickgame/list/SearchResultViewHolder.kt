package com.boardly.pickgame.list

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import com.boardly.constants.PICKED_GAME
import com.boardly.pickgame.PickGameActivity
import com.boardly.retrofit.gameservice.models.SearchResult
import kotlinx.android.synthetic.main.item_search_result.view.gameNameTextView
import kotlinx.android.synthetic.main.item_search_result.view.yearPublishedTextView

class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val parentActivity = itemView.context as PickGameActivity

    fun bind(searchResult: SearchResult) {
        with(itemView) {
            val resultIntent = Intent()
            resultIntent.putExtra(PICKED_GAME, searchResult)

            gameNameTextView.text = searchResult.name
            yearPublishedTextView.text = searchResult.yearPublished

            setOnClickListener {
                parentActivity.setResult(RESULT_OK, resultIntent)
                parentActivity.hideSoftKeyboard()
                parentActivity.finish()
            }
        }
    }
}