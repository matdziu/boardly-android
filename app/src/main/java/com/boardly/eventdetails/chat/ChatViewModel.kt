package com.boardly.eventdetails.chat

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class ChatViewModel(private val chatInteractor: ChatInteractor,
                    initialState: ChatViewState = ChatViewState()) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(initialState)

    fun bind(chatView: ChatView) {

    }

    fun unbind() {
        compositeDisposable.clear()
    }
}