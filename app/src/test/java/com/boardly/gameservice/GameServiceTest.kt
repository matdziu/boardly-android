package com.boardly.gameservice

import com.boardly.retrofit.gameservice.BoardGameGeekService
import com.boardly.retrofit.gameservice.GameServiceImpl
import com.boardly.retrofit.gameservice.models.DetailsResponse
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import org.junit.Test

class GameServiceTest {

    private val boardGameGeekService: BoardGameGeekService = mock {
        on { it.boardGameDetails(any()) } doReturn Observable.just(DetailsResponse())
        on { it.rpgDetails(any()) } doReturn Observable.just(DetailsResponse())
    }
    private val gameService = GameServiceImpl(boardGameGeekService)

    @Test
    fun testDetailsFetchingWithRpgType() {
        gameService.gameDetails("1234rpg")
        verify(boardGameGeekService, times(1)).rpgDetails(any())
        verify(boardGameGeekService, times(0)).boardGameDetails(any())
    }

    @Test
    fun testDetailsFetchingWithBoardGameType() {
        gameService.gameDetails("1234")
        verify(boardGameGeekService, times(0)).rpgDetails(any())
        verify(boardGameGeekService, times(1)).boardGameDetails(any())
    }
}