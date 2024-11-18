package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.discover.DiscoverInteractor
import com.boardly.discover.DiscoverViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class DiscoverViewModelFactory @Inject constructor(private val discoverInteractor: DiscoverInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiscoverViewModel(discoverInteractor) as T
    }
}