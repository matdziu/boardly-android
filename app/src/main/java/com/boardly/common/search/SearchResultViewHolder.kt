package com.boardly.common.search

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.boardly.R
import com.boardly.base.BaseSearchActivity
import com.boardly.constants.PICKED_SEARCH_RESULT

class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val parentActivity = itemView.context as BaseSearchActivity

    fun bind(searchResultData: SearchResultData) {
        with(itemView) {
            val resultIntent = Intent()
            resultIntent.putExtra(PICKED_SEARCH_RESULT, searchResultData)

            val searchResultTitle = this.findViewById<TextView>(R.id.searchResultTitle)
            val searchResultSubtitle = this.findViewById<TextView>(R.id.searchResultSubtitle)

            searchResultTitle.text = searchResultData.title
            searchResultSubtitle.text = searchResultData.subtitle

            setOnClickListener {
                parentActivity.setResult(RESULT_OK, resultIntent)
                parentActivity.hideSoftKeyboard()
                parentActivity.finish()
            }
        }
    }
}