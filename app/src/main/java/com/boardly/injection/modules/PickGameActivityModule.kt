package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.pickgame.PickGameInteractor
import dagger.Module
import dagger.Provides

@Module
class PickGameActivityModule {

    @Provides
    @ActivityScope
    fun providePickGameInteractor(): PickGameInteractor {
        return PickGameInteractor()
    }
}