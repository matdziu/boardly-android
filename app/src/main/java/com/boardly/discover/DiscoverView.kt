package com.boardly.discover

import com.boardly.discover.models.FilteredFetchData
import io.reactivex.Observable

interface DiscoverView {

    fun fetchPlacesListTrigger(): Observable<FilteredFetchData>

    fun render(discoverViewState: DiscoverViewState)
}