package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.login.network.LoginService
import com.boardly.login.network.LoginServiceImpl
import dagger.Module
import dagger.Provides

@Module
class LoginActivityModule {

    @Provides
    @ActivityScope
    fun provideLoginService(): LoginService {
        return LoginServiceImpl()
    }
}