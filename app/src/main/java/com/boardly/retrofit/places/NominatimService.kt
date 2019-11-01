package com.boardly.retrofit.places

import com.boardly.retrofit.places.models.PlaceSearchResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimService {

    @GET("search?format=json")
    fun search(@Query("q") query: String): Observable<List<PlaceSearchResult>>
}