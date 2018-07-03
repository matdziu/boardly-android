package com.boardly.pickgame

import com.boardly.base.BaseInteractor
import com.boardly.retrofit.gamesearch.GameSearchService
import javax.inject.Inject

class PickGameInteractor @Inject constructor(private val gameSearchService: GameSearchService)
    : BaseInteractor() {
}