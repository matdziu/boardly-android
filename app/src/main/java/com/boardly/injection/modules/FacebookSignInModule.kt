package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import dagger.Module
import dagger.Provides

@Module
class FacebookSignInModule {

    @Provides
    @ActivityScope
    fun provideLoginManager(): LoginManager {
        return LoginManager.getInstance()
    }

    @Provides
    @ActivityScope
    fun provideCallbackManager(): CallbackManager {
        return CallbackManager.Factory.create()
    }
}