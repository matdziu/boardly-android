package com.boardly.gamescollection

import com.boardly.gamescollection.network.GamesCollectionService
import com.boardly.retrofit.gameservice.GameService
import javax.inject.Inject

class GamesCollectionInteractor @Inject constructor(private val gameService: GameService,
                                                    private val gamesCollectionService: GamesCollectionService) {

    
}