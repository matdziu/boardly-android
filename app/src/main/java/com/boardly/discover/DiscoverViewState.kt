package com.boardly.discover

import com.boardly.discover.models.Place

data class DiscoverViewState(val progress: Boolean = true,
                             val placesList: List<Place> = listOf())