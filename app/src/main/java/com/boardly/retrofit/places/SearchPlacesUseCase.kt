package com.boardly.retrofit.places

import com.boardly.extensions.noSpecialChars
import com.boardly.retrofit.places.models.PlaceSearchResult
import io.reactivex.Observable
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchPlacesUseCase @Inject constructor(private val nominatimService: NominatimService) {

    private var latestQuery = ""

    fun search(query: String): Observable<List<PlaceSearchResult>> {
        latestQuery = query
        val formattedQuery = query.trim()
            .toLowerCase(Locale.ENGLISH)
            .noSpecialChars()
        return nominatimService.search(formattedQuery)
            .onErrorReturn {
                println(it.toString())
                listOf()
            }
            .filter { query == latestQuery }
    }
}