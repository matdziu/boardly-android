package com.boardly.discover

import com.boardly.filter.models.Filter
import io.reactivex.Observable

interface DiscoverView {

    fun fetchPlacesListTrigger(): Observable<Filter>

    fun render(discoverViewState: DiscoverViewState)
}