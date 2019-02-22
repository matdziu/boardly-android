package com.boardly.injection.modules

import com.boardly.gamescollection.network.GamesCollectionService
import com.boardly.gamescollection.network.GamesCollectionServiceImpl
import com.boardly.injection.ActivityScope
import com.boardly.retrofit.gameservice.GameService
import com.boardly.retrofit.gameservice.GameServiceImpl
import dagger.Module
import dagger.Provides

@Module
class GamesCollectionActivityModule {

    @Provides
    @ActivityScope
    fun provideGamesCollectionService(): GamesCollectionService {
        return GamesCollectionServiceImpl()
    }

    @Provides
    @ActivityScope
    fun provideGameService(gameServiceImpl: GameServiceImpl): GameService = gameServiceImpl
}