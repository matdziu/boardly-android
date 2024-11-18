package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.gamescollection.GamesCollectionInteractor
import com.boardly.gamescollection.GamesCollectionViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class GamesCollectionViewModelFactory @Inject constructor(private val gamesCollectionInteractor: GamesCollectionInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GamesCollectionViewModel(gamesCollectionInteractor) as T
    }
}