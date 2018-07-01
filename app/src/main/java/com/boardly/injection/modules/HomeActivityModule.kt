package com.boardly.injection.modules

import com.boardly.home.HomeInteractor
import com.boardly.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class HomeActivityModule {

    @Provides
    @ActivityScope
    fun provideHomeInteractor(): HomeInteractor {
        return HomeInteractor()
    }
}