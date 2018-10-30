package com.boardly.myevents

import android.support.annotation.StringRes
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boardly.R
import com.boardly.common.events.list.EventsAdapter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_my_events.view.myEventsRecyclerView
import kotlinx.android.synthetic.main.view_my_events.view.noEventsTextView

class ViewPagerAdapter : PagerAdapter() {

    val acceptedAdapter = EventsAdapter()
    val pendingAdapter = EventsAdapter()
    val createdAdapter = EventsAdapter()
    val interestingAdapter = EventsAdapter()

    private val viewsSubject = PublishSubject.create<View>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pageView = LayoutInflater.from(container.context).inflate(R.layout.view_my_events, container, false)
        val pageEnum = PageView.values()[position]
        when (pageEnum) {
            PageView.ACCEPTED -> init(acceptedAdapter, R.string.no_accepted_events_found_text, pageView, pageEnum)
            PageView.CREATED -> init(createdAdapter, R.string.no_created_events_found_text, pageView, pageEnum)
            PageView.PENDING -> init(pendingAdapter, R.string.no_pending_events_found_text, pageView, pageEnum)
            PageView.INTERESTING -> init(interestingAdapter, R.string.no_interesting_events_found_text, pageView, pageEnum)
        }

        container.addView(pageView)
        viewsSubject.onNext(pageView)

        return pageView
    }

    private fun init(adapter: EventsAdapter,
                     @StringRes noEventsHintId: Int,
                     pageView: View,
                     tagObj: Any): View {
        return pageView.apply {
            myEventsRecyclerView.layoutManager = LinearLayoutManager(pageView.context)
            myEventsRecyclerView.adapter = adapter
            noEventsTextView.setText(noEventsHintId)
            tag = tagObj
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

    fun renderingFinishedEmitter(): Observable<View> = viewsSubject
}

enum class PageView {
    ACCEPTED, CREATED, PENDING, INTERESTING
}