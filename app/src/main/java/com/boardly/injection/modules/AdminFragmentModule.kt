package com.boardly.injection.modules

import android.support.v7.app.AppCompatActivity
import com.boardly.eventdetails.EventDetailsActivity
import com.boardly.eventdetails.admin.network.AdminService
import com.boardly.eventdetails.admin.network.AdminServiceImpl
import com.boardly.injection.FragmentScope
import dagger.Module
import dagger.Provides

@Module
class AdminFragmentModule {

    @Provides
    @FragmentScope
    fun provideAdminService(): AdminService {
        return AdminServiceImpl()
    }

    @Provides
    @FragmentScope
    fun provideAppCompatActivity(eventDetailsActivity: EventDetailsActivity): AppCompatActivity = eventDetailsActivity
}