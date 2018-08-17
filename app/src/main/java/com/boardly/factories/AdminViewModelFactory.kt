package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.eventdetails.admin.AdminInteractor
import com.boardly.eventdetails.admin.AdminViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class AdminViewModelFactory @Inject constructor(private val adminInteractor: AdminInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AdminViewModel(adminInteractor) as T
    }
}