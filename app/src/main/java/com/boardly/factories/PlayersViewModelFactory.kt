package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.eventdetails.players.PlayersInteractor
import com.boardly.eventdetails.players.PlayersViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class PlayersViewModelFactory @Inject constructor(private val playersInteractor: PlayersInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayersViewModel(playersInteractor) as T
    }
}