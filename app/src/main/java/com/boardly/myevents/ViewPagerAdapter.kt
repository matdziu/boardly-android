package com.boardly.myevents

import android.support.annotation.StringRes
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.R
import com.boardly.common.events.list.EventsAdapter
import kotlinx.android.synthetic.main.view_my_events.view.myEventsRecyclerView
import kotlinx.android.synthetic.main.view_my_events.view.noEventsTextView

class ViewPagerAdapter(private val acceptedEventsAdapter: EventsAdapter,
                       private val pendingEventsAdapter: EventsAdapter,
                       private val createdEventsAdapter: EventsAdapter) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pageView = LayoutInflater.from(container.context).inflate(R.layout.view_my_events, container, true)
        when (PageView.values()[position]) {
            PageView.ACCEPTED -> assignAdapterAndHint(acceptedEventsAdapter, R.string.no_accepted_events_found_text, pageView)
            PageView.CREATED -> assignAdapterAndHint(createdEventsAdapter, R.string.no_created_events_found_text, pageView)
            PageView.PENDING -> assignAdapterAndHint(pendingEventsAdapter, R.string.no_pending_events_found_text, pageView)
        }
        return pageView
    }

    private fun assignAdapterAndHint(adapter: EventsAdapter, @StringRes hintStringId: Int, pageView: View) {
        with(pageView) {
            myEventsRecyclerView.adapter = adapter
            noEventsTextView.setText(hintStringId)
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeViewAt(position)
    }

    override fun getCount(): Int {
        return PageView.values().size
    }
}

enum class PageView {
    ACCEPTED, CREATED, PENDING
}