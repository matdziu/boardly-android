package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.signup.SignUpActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ActivityScope
    @ContributesAndroidInjector(modules = [SignUpActivityModule::class])
    abstract fun bindSignUpActivity(): SignUpActivity
}