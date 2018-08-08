package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.signup.network.SignUpService
import com.boardly.signup.network.SignUpServiceImpl
import dagger.Module
import dagger.Provides

@Module
class SignUpActivityModule {

    @Provides
    @ActivityScope
    fun provideSignUpService(): SignUpService {
        return SignUpServiceImpl()
    }
}