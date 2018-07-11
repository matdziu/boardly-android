package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.filter.FilterInteractor
import com.boardly.filter.FilterViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class FilterViewModelFactory @Inject constructor(private val filterInteractor: FilterInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FilterViewModel(filterInteractor) as T
    }
}