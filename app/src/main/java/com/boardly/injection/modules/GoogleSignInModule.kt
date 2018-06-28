package com.boardly.injection.modules

import android.content.Context
import com.boardly.BuildConfig
import com.boardly.injection.ActivityScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides


@Module
class GoogleSignInModule {

    @Provides
    @ActivityScope
    fun provideGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.OAUTH_CLIENT_ID)
                .requestEmail()
                .build()
    }

    @Provides
    @ActivityScope
    fun provideGoogleSignInClient(context: Context, googleSignInOptions: GoogleSignInOptions): GoogleSignInClient {
        return GoogleSignIn.getClient(context, googleSignInOptions)
    }
}