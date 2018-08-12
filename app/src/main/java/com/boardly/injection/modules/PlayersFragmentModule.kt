package com.boardly.injection.modules

import com.boardly.eventdetails.players.network.PlayersService
import com.boardly.eventdetails.players.network.PlayersServiceImpl
import com.boardly.injection.FragmentScope
import dagger.Module
import dagger.Provides

@Module
class PlayersFragmentModule {

    @Provides
    @FragmentScope
    fun providePlayersService(): PlayersService {
        return PlayersServiceImpl()
    }
}