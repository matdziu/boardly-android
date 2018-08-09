package com.boardly.pickgame

import com.boardly.retrofit.gamesearch.models.SearchResult

sealed class PartialPickGameViewState {

    abstract fun reduce(previousState: PickGameViewState): PickGameViewState

    class ProgressState : PartialPickGameViewState() {
        override fun reduce(previousState: PickGameViewState) = PickGameViewState(progress = true)
    }

    data class ResultsFetchedState(private val searchResults: List<SearchResult>) : PartialPickGameViewState() {
        override fun reduce(previousState: PickGameViewState): PickGameViewState {
            return PickGameViewState(searchResults = searchResults)
        }
    }

    data class ErrorState(private val throwable: Throwable, private val unacceptedQuery: String) : PartialPickGameViewState() {
        override fun reduce(previousState: PickGameViewState): PickGameViewState {
            return PickGameViewState(error = throwable, unacceptedQuery = unacceptedQuery)
        }
    }
}