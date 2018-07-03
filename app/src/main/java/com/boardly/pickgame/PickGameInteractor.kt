package com.boardly.pickgame

import android.util.Log
import com.boardly.base.BaseInteractor
import com.boardly.retrofit.gamesearch.GameSearchService
import javax.inject.Inject

class PickGameInteractor @Inject constructor(private val gameSearchService: GameSearchService)
    : BaseInteractor() {

    init {
        gameSearchService.search("galaxy truck").subscribe {
            Log.d("mateusz", it.toString())
        }
    }
}