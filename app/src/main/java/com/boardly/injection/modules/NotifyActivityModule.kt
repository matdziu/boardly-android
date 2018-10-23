package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.notify.network.NotifyService
import com.boardly.notify.network.NotifyServiceImpl
import com.boardly.retrofit.gamesearch.GameSearchService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class NotifyActivityModule {

    @Provides
    @ActivityScope
    fun provideGameSearchService(retrofit: Retrofit): GameSearchService {
        return retrofit.create(GameSearchService::class.java)
    }

    @Provides
    @ActivityScope
    fun provideNotifyService(): NotifyService {
        return NotifyServiceImpl()
    }
}