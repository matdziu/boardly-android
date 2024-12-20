package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.analytics.Analytics
import com.boardly.eventdetails.admin.AdminInteractor
import com.boardly.eventdetails.admin.AdminViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class AdminViewModelFactory @Inject constructor(private val adminInteractor: AdminInteractor,
                                                private val analytics: Analytics) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewModel(adminInteractor, analytics) as T
    }
}