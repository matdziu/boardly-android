package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.manageplace.network.ManagePlaceService
import com.boardly.manageplace.network.ManagePlaceServiceImpl
import dagger.Module
import dagger.Provides

@Module
class ManagePlaceActivityModule {

    @Provides
    @ActivityScope
    fun provideManagePlaceService(): ManagePlaceService {
        return ManagePlaceServiceImpl()
    }
}