package com.boardly.pickgame

import com.boardly.retrofit.gamesearch.models.SearchResult

sealed class PartialPickGameViewState {

    abstract fun reduce(previousState: PickGameViewState): PickGameViewState

    class ProgressState : PartialPickGameViewState() {
        override fun reduce(previousState: PickGameViewState) = PickGameViewState(progress = true)
    }

    class ResultsFetchedState(private val searchResults: List<SearchResult>) : PartialPickGameViewState() {
        override fun reduce(previousState: PickGameViewState): PickGameViewState {
            return PickGameViewState(searchResults = searchResults)
        }
    }
}