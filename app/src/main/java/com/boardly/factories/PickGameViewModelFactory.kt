package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.pickgame.PickGameInteractor
import com.boardly.pickgame.PickGameViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class PickGameViewModelFactory @Inject constructor(private val pickGameInteractor: PickGameInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PickGameViewModel(pickGameInteractor) as T
    }
}