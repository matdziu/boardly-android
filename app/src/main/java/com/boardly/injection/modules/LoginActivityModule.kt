package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.login.LoginInteractor
import dagger.Module
import dagger.Provides

@Module
class LoginActivityModule {

    @Provides
    @ActivityScope
    fun provideLoginInteractor(): LoginInteractor {
        return LoginInteractor()
    }
}