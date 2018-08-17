package com.boardly.injection.modules

import com.boardly.eventdetails.admin.AdminFragment
import com.boardly.eventdetails.players.PlayersFragment
import com.boardly.injection.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {

    @FragmentScope
    @ContributesAndroidInjector(modules = [PlayersFragmentModule::class])
    abstract fun bindPlayersFragment(): PlayersFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [AdminFragmentModule::class])
    abstract fun bindAdminFragment(): AdminFragment
}