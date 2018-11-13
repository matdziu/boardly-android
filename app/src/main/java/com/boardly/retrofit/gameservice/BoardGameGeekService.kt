package com.boardly.retrofit.gameservice

import com.boardly.retrofit.gameservice.models.DetailsResponse
import com.boardly.retrofit.gameservice.models.SearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface BoardGameGeekService {

    @GET("search?type=boardgame,rpg")
    fun search(@Query("query") query: String): Observable<SearchResponse>

    @GET("thing&type=boardgame")
    fun boardGameDetails(@Query("id") id: String): Observable<DetailsResponse>

    @GET("family&type=rpg")
    fun rpgDetails(@Query("id") id: String): Observable<DetailsResponse>
}