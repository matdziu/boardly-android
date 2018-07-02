package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.pickcity.PickCityInteractor
import com.boardly.pickcity.PickCityViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class PickCityViewModelFactory @Inject constructor(private val pickCityInteractor: PickCityInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PickCityViewModel(pickCityInteractor) as T
    }
}