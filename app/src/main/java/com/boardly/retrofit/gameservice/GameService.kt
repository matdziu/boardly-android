package com.boardly.retrofit.gameservice

import com.boardly.retrofit.gameservice.models.DetailsResponse
import com.boardly.retrofit.gameservice.models.SearchResponse
import io.reactivex.Observable

interface GameService {

    fun search(query: String): Observable<SearchResponse>

    fun gameDetails(id: String): Observable<DetailsResponse>
}