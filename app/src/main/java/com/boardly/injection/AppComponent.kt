package com.boardly.injection

import com.boardly.BoardlyApplication
import com.boardly.injection.modules.ActivityBuilder
import com.boardly.injection.modules.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Component(modules = [
    AndroidInjectionModule::class,
    ActivityBuilder::class,
    AppModule::class])
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(boardlyApplication: BoardlyApplication): Builder

        fun build(): AppComponent
    }

    fun inject(boardlyApplication: BoardlyApplication)
}