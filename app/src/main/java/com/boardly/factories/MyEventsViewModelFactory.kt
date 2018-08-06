package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.myevents.MyEventsInteractor
import com.boardly.myevents.MyEventsViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MyEventsViewModelFactory @Inject constructor(private val myEventsInteractor: MyEventsInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MyEventsViewModel(myEventsInteractor) as T
    }
}