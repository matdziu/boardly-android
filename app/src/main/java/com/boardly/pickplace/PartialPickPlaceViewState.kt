package com.boardly.pickplace

import com.boardly.common.search.SearchResultData

sealed class PartialPickPlaceViewState {

    abstract fun reduce(previousState: PickPlaceViewState): PickPlaceViewState

    object ProgressState : PartialPickPlaceViewState() {
        override fun reduce(previousState: PickPlaceViewState) = PickPlaceViewState(progress = true)
    }

    data class ResultsFetchedState(private val searchResults: List<SearchResultData>) : PartialPickPlaceViewState() {
        override fun reduce(previousState: PickPlaceViewState): PickPlaceViewState {
            return PickPlaceViewState(searchResults = searchResults)
        }
    }

    data class ErrorState(private val throwable: Throwable, private val unacceptedQuery: String) : PartialPickPlaceViewState() {
        override fun reduce(previousState: PickPlaceViewState): PickPlaceViewState {
            return PickPlaceViewState(error = throwable, unacceptedQuery = unacceptedQuery)
        }
    }
}