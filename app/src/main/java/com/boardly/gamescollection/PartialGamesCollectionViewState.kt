package com.boardly.gamescollection

import com.boardly.gamescollection.models.CollectionGame

sealed class PartialGamesCollectionViewState {

    abstract fun reduce(previousState: GamesCollectionViewState): GamesCollectionViewState

    object ProgressState : PartialGamesCollectionViewState() {
        override fun reduce(previousState: GamesCollectionViewState): GamesCollectionViewState {
            return previousState.copy(
                    progress = true)
        }
    }

    data class CollectionFetched(val games: List<CollectionGame>) : PartialGamesCollectionViewState() {
        override fun reduce(previousState: GamesCollectionViewState): GamesCollectionViewState {
            return previousState.copy(
                    progress = false,
                    games = games)
        }
    }

    data class SuccessState(private val render: Boolean = true) : PartialGamesCollectionViewState() {
        override fun reduce(previousState: GamesCollectionViewState): GamesCollectionViewState {
            return previousState.copy(
                    progress = false,
                    success = render)
        }
    }

    data class NoMoreLimitState(private val render: Boolean = true) : PartialGamesCollectionViewState() {
        override fun reduce(previousState: GamesCollectionViewState): GamesCollectionViewState {
            return previousState.copy(
                    progress = false,
                    noMoreLimit = render)
        }
    }
}