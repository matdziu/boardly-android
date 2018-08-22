package com.boardly.common.utils

import android.support.v7.widget.RecyclerView

class MessagesAdapterObserver(private val scroller: (Int) -> Unit) : RecyclerView.AdapterDataObserver() {

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        scroller(positionStart)
    }
}