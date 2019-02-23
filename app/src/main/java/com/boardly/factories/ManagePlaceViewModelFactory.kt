package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.manageplace.ManagePlaceInteractor
import com.boardly.manageplace.ManagePlaceViewModel
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class ManagePlaceViewModelFactory @Inject constructor(private val managePlaceInteractor: ManagePlaceInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ManagePlaceViewModel(managePlaceInteractor) as T
    }
}