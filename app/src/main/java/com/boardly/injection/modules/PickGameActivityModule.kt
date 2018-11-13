package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.retrofit.gameservice.BoardGameGeekService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class PickGameActivityModule {

    @Provides
    @ActivityScope
    fun provideGameSearchService(retrofit: Retrofit): BoardGameGeekService {
        return retrofit.create(BoardGameGeekService::class.java)
    }
}