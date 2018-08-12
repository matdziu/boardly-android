package com.boardly.injection.modules

import com.boardly.addevent.AddEventActivity
import com.boardly.editprofile.EditProfileActivity
import com.boardly.eventdetails.EventDetailsActivity
import com.boardly.filter.FilterActivity
import com.boardly.home.HomeActivity
import com.boardly.injection.ActivityScope
import com.boardly.login.LoginActivity
import com.boardly.myevents.MyEventsActivity
import com.boardly.pickgame.PickGameActivity
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
    @ContributesAndroidInjector(modules = [EditProfileActivityModule::class])
    abstract fun bindEditProfileActivity(): EditProfileActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [HomeActivityModule::class])
    abstract fun bindHomeActivity(): HomeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [AddEventActivityModule::class])
    abstract fun bindAddEventActivity(): AddEventActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PickGameActivityModule::class])
    abstract fun bindPickGameActivity(): PickGameActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [FilterActivityModule::class])
    abstract fun bindFilterActivity(): FilterActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MyEventsActivityModule::class])
    abstract fun bindMyEventsActivity(): MyEventsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [FragmentBuilder::class])
    abstract fun bindEventDetailsActivity(): EventDetailsActivity
}