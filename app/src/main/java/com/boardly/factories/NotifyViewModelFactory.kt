package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.notify.NotifyInteractor
import com.boardly.notify.NotifyViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class NotifyViewModelFactory @Inject constructor(private val notifyInteractor: NotifyInteractor)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotifyViewModel(notifyInteractor) as T
    }
}