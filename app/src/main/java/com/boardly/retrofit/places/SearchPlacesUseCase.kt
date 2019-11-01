package com.boardly.retrofit.places

import com.boardly.retrofit.places.models.PlaceSearchResult
import io.reactivex.Observable

interface SearchPlacesUseCase {

    fun search(query: String): Observable<List<PlaceSearchResult>>
}