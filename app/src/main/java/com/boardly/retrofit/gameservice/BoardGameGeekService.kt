package com.boardly.retrofit.gameservice

import com.boardly.BuildConfig
import com.boardly.retrofit.gameservice.models.DetailsResponse
import com.boardly.retrofit.gameservice.models.SearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface BoardGameGeekService {

    @GET("${BuildConfig.BGG_API_URL}search?type=boardgame,rpg")
    fun search(@Query("query") query: String): Observable<SearchResponse>

    @GET("${BuildConfig.BGG_API_URL}thing&type=boardgame")
    fun boardGameDetails(@Query("id") id: String): Observable<DetailsResponse>

    @GET("${BuildConfig.BGG_API_URL}family&type=rpg")
    fun rpgDetails(@Query("id") id: String): Observable<DetailsResponse>
}