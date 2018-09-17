package com.boardly.myevents

import android.support.annotation.StringRes
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.R
import com.boardly.common.events.list.EventsAdapter
import com.boardly.common.events.models.Event
import kotlinx.android.synthetic.main.view_my_events.view.myEventsRecyclerView
import kotlinx.android.synthetic.main.view_my_events.view.noEventsTextView

class ViewPagerAdapter(private val acceptedEvents: List<Event>,
                       private val pendingEvents: List<Event>,
                       private val createdEvents: List<Event>) : PagerAdapter() {

    private val acceptedAdapter = EventsAdapter()
    private val pendingAdapter = EventsAdapter()
    private val createdAdapter = EventsAdapter()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pageView = LayoutInflater.from(container.context).inflate(R.layout.view_my_events, container, false)
        when (PageView.values()[position]) {
            PageView.ACCEPTED -> {
                init(acceptedAdapter, R.string.no_accepted_events_found_text, pageView)
                display(acceptedAdapter, acceptedEvents, pageView)
            }
            PageView.CREATED -> {
                init(createdAdapter, R.string.no_created_events_found_text, pageView)
                display(createdAdapter, createdEvents, pageView)
            }
            PageView.PENDING -> {
                init(pendingAdapter, R.string.no_pending_events_found_text, pageView)
                display(pendingAdapter, pendingEvents, pageView)
            }
        }
        container.addView(pageView)

        return pageView
    }

    private fun init(adapter: EventsAdapter, @StringRes noEventsHintId: Int, pageView: View): View {
        return pageView.apply {
            myEventsRecyclerView.layoutManager = LinearLayoutManager(pageView.context)
            myEventsRecyclerView.adapter = adapter
            noEventsTextView.setText(noEventsHintId)
        }
    }

    private fun display(adapter: EventsAdapter, eventList: List<Event>, pageView: View) {
        with(pageView) {
            if (eventList.isNotEmpty()) {
                noEventsTextView.visibility = View.GONE
                myEventsRecyclerView.visibility = View.VISIBLE
                adapter.submitList(eventList)
            } else {
                noEventsTextView.visibility = View.VISIBLE
                myEventsRecyclerView.visibility = View.GONE
            }
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int {
        return PageView.values().size
    }
}

enum class PageView {
    ACCEPTED, CREATED, PENDING
}