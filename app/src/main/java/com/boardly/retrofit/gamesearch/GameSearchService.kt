package com.boardly.retrofit.gamesearch

import com.boardly.retrofit.gamesearch.models.Game
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GameSearchService {

    @GET("search")
    fun search(@Query("search") query: String): Observable<Game>
}