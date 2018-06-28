package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.signup.SignUpInteractor
import dagger.Module
import dagger.Provides

@Module
class SignUpActivityModule {

    @Provides
    @ActivityScope
    fun provideSignUpInteractor(): SignUpInteractor {
        return SignUpInteractor()
    }
}