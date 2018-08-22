package com.boardly.eventdetails.chat

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class ChatViewModel(private val chatInteractor: ChatInteractor,
                    initialState: ChatViewState = ChatViewState()) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(initialState)

    fun bind(chatView: ChatView, eventId: String) {
        val newMessagesListenerToggleObservable = chatView.newMessagesListenerToggleEmitter()
                .flatMap {
                    if (it) chatInteractor.listenForNewMessages(eventId)
                    else chatInteractor.stopListeningForNewMessages(eventId)
                }

        val batchFetchTriggerObservable = chatView.batchFetchTriggerEmitter()
                .flatMap { chatInteractor.fetchChatMessagesBatch(eventId, it) }

        val messageObservable = chatView.messageEmitter()
                .filter { it.isNotBlank() }
                .flatMap { chatInteractor.sendMessage(it.trim(), eventId) }

        val mergedObservable = Observable.merge(
                newMessagesListenerToggleObservable,
                batchFetchTriggerObservable,
                messageObservable)
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { chatView.render(it) })
    }

    private fun reduce(previousState: ChatViewState, partialState: PartialChatViewState): ChatViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}