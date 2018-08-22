package com.boardly.injection.modules

import com.boardly.eventdetails.chat.network.ChatService
import com.boardly.eventdetails.chat.network.ChatServiceImpl
import com.boardly.injection.FragmentScope
import dagger.Module
import dagger.Provides

@Module
class ChatFragmentModule {

    @Provides
    @FragmentScope
    fun provideChatService(): ChatService {
        return ChatServiceImpl()
    }
}