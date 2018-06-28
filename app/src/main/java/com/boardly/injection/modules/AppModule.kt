package com.boardly.injection.modules

import android.content.Context
import com.boardly.BoardlyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(boardlyApplication: BoardlyApplication): Context {
        return boardlyApplication
    }
}