package com.boardly.retrofit.gameservice

import com.boardly.constants.RPG_TYPE
import com.boardly.extensions.clearFromType
import com.boardly.extensions.isOfType
import com.boardly.retrofit.gameservice.models.DetailsResponse
import com.boardly.retrofit.gameservice.models.SearchResponse
import io.reactivex.Observable
import javax.inject.Inject

class GameServiceImpl @Inject constructor(private val boardGameGeekService: BoardGameGeekService)
    : GameService {

    override fun search(query: String): Observable<SearchResponse> = boardGameGeekService.search(query)

    override fun gameDetails(id: String): Observable<DetailsResponse> {
        return if (id.isOfType(RPG_TYPE)) boardGameGeekService.rpgDetails(id.clearFromType(RPG_TYPE))
        else boardGameGeekService.boardGameDetails(id)
    }
}