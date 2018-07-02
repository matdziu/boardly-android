package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.pickcity.PickCityInteractor
import dagger.Module
import dagger.Provides

@Module
class PickCityActivityModule {

    @Provides
    @ActivityScope
    fun providePickCityInteractor(): PickCityInteractor {
        return PickCityInteractor()
    }
}