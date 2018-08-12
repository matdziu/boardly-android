package com.boardly.eventdetails

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.boardly.eventdetails.chat.ChatFragment
import com.boardly.eventdetails.players.PlayersFragment

class ViewPagerAdapter(fragmentManager: FragmentManager,
                       private val tabsCount: Int,
                       private val eventId: String) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ChatFragment.newInstance()
            1 -> PlayersFragment.newInstance(eventId)
            else -> PlayersFragment.newInstance(eventId)
        }
    }

    override fun getCount(): Int = tabsCount
}