package com.boardly.retrofit.places

import com.boardly.retrofit.places.models.PlaceSearchResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface NominatimService {

    @GET("https://nominatim.openstreetmap.org/search?q={query}&format=json")
    fun search(@Path("query") query: String): Observable<List<PlaceSearchResult>>
}