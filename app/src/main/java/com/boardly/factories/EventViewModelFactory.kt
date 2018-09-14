package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.analytics.Analytics
import com.boardly.event.EventInteractor
import com.boardly.event.EventViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class EventViewModelFactory @Inject constructor(private val eventInteractor: EventInteractor,
                                                private val analytics: Analytics) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventViewModel(eventInteractor, analytics) as T
    }
}