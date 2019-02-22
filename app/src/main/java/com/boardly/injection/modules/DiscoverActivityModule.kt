package com.boardly.injection.modules

import com.boardly.discover.network.DiscoverService
import com.boardly.discover.network.DiscoverServiceImpl
import com.boardly.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class DiscoverActivityModule {

    @Provides
    @ActivityScope
    fun provideDiscoverService(): DiscoverService {
        return DiscoverServiceImpl()
    }
}