package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.myevents.network.MyEventsService
import com.boardly.myevents.network.MyEventsServiceImpl
import dagger.Module
import dagger.Provides

@Module
class MyEventsActivityModule {

    @Provides
    @ActivityScope
    fun provideMyEventsService(): MyEventsService {
        return MyEventsServiceImpl()
    }
}