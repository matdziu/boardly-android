package com.boardly.injection.modules

import com.boardly.addevent.AddEventInteractor
import com.boardly.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class AddEventActivityModule {

    @Provides
    @ActivityScope
    fun provideAddEventInteractor(): AddEventInteractor {
        return AddEventInteractor()
    }
}