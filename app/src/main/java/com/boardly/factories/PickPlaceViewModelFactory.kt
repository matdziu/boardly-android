package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.pickplace.PickPlaceViewModel
import com.boardly.retrofit.places.SearchPlacesUseCase
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class PickPlaceViewModelFactory @Inject constructor(private val searchPlacesUseCase: SearchPlacesUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PickPlaceViewModel(searchPlacesUseCase) as T
    }
}