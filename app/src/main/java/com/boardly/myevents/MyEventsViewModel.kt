package com.boardly.myevents

import android.arch.lifecycle.ViewModel
import com.boardly.analytics.Analytics
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class MyEventsViewModel(private val myEventsInteractor: MyEventsInteractor,
                        private val analytics: Analytics) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(MyEventsViewState())

    fun bind(myEventsView: MyEventsView) {
        val eventsFetchObservable = myEventsView.fetchEventsTriggerEmitter()
                .flatMap {
                    val myEventsObservable = myEventsInteractor.fetchEvents()
                    return@flatMap when (it) {
                        true -> myEventsObservable.startWith(PartialMyEventsViewState.ProgressState())
                        false -> myEventsObservable
                    }
                }

        val joinEventObservable = myEventsView.joinEventEmitter()
                .flatMap {
                    analytics.logJoinRequestSentEvent()
                    myEventsInteractor.joinEvent(it)
                }

        val updateEventListObservable = myEventsView.joinEventEmitter()
                .map { joinEventData ->
                    val currentState = stateSubject.value ?: MyEventsViewState()
                    val eventToBeJoined = currentState.interestingEvents.find { joinEventData.eventId != it.eventId }
                    val acceptedEvents = currentState.acceptedEvents
                    val createdEvents = currentState.createdEvents
                    if (eventToBeJoined != null) {
                        val pendingEvents = listOf(eventToBeJoined) + currentState.pendingEvents
                        val interestingEvents = currentState.interestingEvents.filter { it == eventToBeJoined }
                        PartialMyEventsViewState.EventsFetchedState(
                                acceptedEvents,
                                pendingEvents,
                                createdEvents,
                                interestingEvents)
                    } else {
                        PartialMyEventsViewState.EventsFetchedState(
                                acceptedEvents,
                                currentState.pendingEvents,
                                createdEvents,
                                currentState.interestingEvents)
                    }
                }

        val mergedObservable = Observable.merge(listOf(
                eventsFetchObservable,
                joinEventObservable,
                updateEventListObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { myEventsView.render(it) })
    }

    private fun reduce(previousState: MyEventsViewState, partialState: PartialMyEventsViewState)
            : MyEventsViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}