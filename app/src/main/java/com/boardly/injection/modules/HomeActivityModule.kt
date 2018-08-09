package com.boardly.injection.modules

import com.boardly.home.network.HomeService
import com.boardly.home.network.HomeServiceImpl
import com.boardly.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class HomeActivityModule {

    @Provides
    @ActivityScope
    fun provideHomeService(): HomeService {
        return HomeServiceImpl()
    }
}