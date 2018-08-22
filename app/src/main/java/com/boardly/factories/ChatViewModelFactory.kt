package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.eventdetails.chat.ChatInteractor
import com.boardly.eventdetails.chat.ChatViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ChatViewModelFactory @Inject constructor(private val chatInteractor: ChatInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(chatInteractor) as T
    }
}