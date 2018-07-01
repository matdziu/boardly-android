package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.home.HomeInteractor
import com.boardly.home.HomeViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory @Inject constructor(private val homeInteractor: HomeInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(homeInteractor) as T
    }
}