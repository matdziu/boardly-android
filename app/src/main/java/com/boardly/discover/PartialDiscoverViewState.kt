package com.boardly.discover

import com.boardly.discover.models.Place

sealed class PartialDiscoverViewState {

    abstract fun reduce(previousState: DiscoverViewState): DiscoverViewState

    data class PlacesListFetched(private val placesList: List<Place>) : PartialDiscoverViewState() {
        override fun reduce(previousState: DiscoverViewState): DiscoverViewState {
            return DiscoverViewState(placesList = placesList)
        }
    }
}