package com.boardly.injection.modules

import com.boardly.editprofile.EditProfileActivity
import com.boardly.injection.ActivityScope
import com.boardly.login.LoginActivity
import com.boardly.signup.SignUpActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ActivityScope
    @ContributesAndroidInjector(modules = [SignUpActivityModule::class])
    abstract fun bindSignUpActivity(): SignUpActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [LoginActivityModule::class, GoogleSignInModule::class,
        FacebookSignInModule::class])
    abstract fun bindLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [EditProfileModule::class])
    abstract fun bindEditProfileActivity(): EditProfileActivity
}