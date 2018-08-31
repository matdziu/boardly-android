package com.boardly.injection.modules

import com.boardly.event.network.EventService
import com.boardly.event.network.EventServiceImpl
import com.boardly.injection.ActivityScope
import com.boardly.retrofit.gamesearch.GameSearchService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class EventActivityModule {

    @Provides
    @ActivityScope
    fun provideGameSearchService(retrofit: Retrofit): GameSearchService {
        return retrofit.create(GameSearchService::class.java)
    }

    @Provides
    @ActivityScope
    fun provideEventService(): EventService {
        return EventServiceImpl()
    }
}