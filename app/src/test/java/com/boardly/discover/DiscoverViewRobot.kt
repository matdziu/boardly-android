package com.boardly.discover

import com.boardly.base.BaseViewRobot
import com.boardly.discover.models.FilteredFetchData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DiscoverViewRobot(discoverViewModel: DiscoverViewModel) : BaseViewRobot<DiscoverViewState>() {

    private val fetchPlacesListSubject = PublishSubject.create<FilteredFetchData>()

    private val discoverView = object : DiscoverView {
        override fun fetchPlacesListTrigger(): Observable<FilteredFetchData> {
            return fetchPlacesListSubject
        }

        override fun render(discoverViewState: DiscoverViewState) {
            renderedStates.add(discoverViewState)
        }
    }

    init {
        discoverViewModel.bind(discoverView)
    }

    fun triggerPlacesFetch(filteredFetchData: FilteredFetchData) {
        fetchPlacesListSubject.onNext(filteredFetchData)
    }
}