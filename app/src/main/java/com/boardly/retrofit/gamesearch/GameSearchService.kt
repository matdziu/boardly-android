package com.boardly.retrofit.gamesearch

import com.boardly.retrofit.gamesearch.models.DetailsResponse
import com.boardly.retrofit.gamesearch.models.SearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GameSearchService {

    @GET("search?type=boardgame,boardgameexpansion")
    fun search(@Query("query") query: String): Observable<SearchResponse>

    @GET("thing")
    fun details(@Query("id") id: String): Observable<DetailsResponse>
}