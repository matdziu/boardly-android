package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.retrofit.gameservice.BoardGameGeekService
import com.boardly.retrofit.gameservice.GameService
import com.boardly.retrofit.gameservice.GameServiceImpl
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class FilterActivityModule {

    @Provides
    @ActivityScope
    fun provideGameService(gameServiceImpl: GameServiceImpl): GameService = gameServiceImpl

    @Provides
    @ActivityScope
    fun provideBoardGameGeekService(retrofit: Retrofit): BoardGameGeekService {
        return retrofit.create(BoardGameGeekService::class.java)
    }
}