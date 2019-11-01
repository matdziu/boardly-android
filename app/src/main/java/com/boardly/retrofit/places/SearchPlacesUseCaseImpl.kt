package com.boardly.retrofit.places

import com.boardly.extensions.noSpecialChars
import com.boardly.retrofit.places.models.PlaceSearchResult
import io.reactivex.Observable
import java.util.Locale
import javax.inject.Inject

class SearchPlacesUseCaseImpl @Inject constructor(private val nominatimService: NominatimService) : SearchPlacesUseCase {

    private var latestQuery = ""

    override fun search(query: String): Observable<List<PlaceSearchResult>> {
        latestQuery = query
        val formattedQuery = query.trim()
                .toLowerCase(Locale.ENGLISH)
                .replace(" ", "+")
                .noSpecialChars()
        return nominatimService.search(formattedQuery)
                .filter { query == latestQuery }
    }
}