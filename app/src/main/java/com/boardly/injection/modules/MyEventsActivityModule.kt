package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.myevents.MyEventsInteractor
import dagger.Module
import dagger.Provides

@Module
class MyEventsActivityModule {

    @Provides
    @ActivityScope
    fun provideMyEventsInteractor(): MyEventsInteractor {
        return MyEventsInteractor()
    }
}