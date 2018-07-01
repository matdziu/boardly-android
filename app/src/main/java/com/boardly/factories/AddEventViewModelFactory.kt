package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.addevent.AddEventInteractor
import com.boardly.addevent.AddEventViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class AddEventViewModelFactory @Inject constructor(private val addEventInteractor: AddEventInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEventViewModel(addEventInteractor) as T
    }
}