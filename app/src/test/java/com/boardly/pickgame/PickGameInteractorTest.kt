package com.boardly.pickgame

import com.boardly.retrofit.gameservice.BoardGameGeekService
import com.boardly.retrofit.gameservice.models.SearchResponse
import com.boardly.retrofit.gameservice.models.SearchResult
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Test

class PickGameInteractorTest {

    @Test
    fun testSuccessfulResultsFetchingWithDelayedResultsScenario() {
        val searchResults = listOf(SearchResult(name = "Monopoly"))
        val delayedSearchResults = listOf(SearchResult(name = "Eurobusiness"))
        val delayedSearchResponseSubject = PublishSubject.create<SearchResponse>()
        val boardGameGeekService: BoardGameGeekService = mock {
            on { it.search("firstQuery") } doReturn delayedSearchResponseSubject
            on { it.search("secondQuery") } doReturn Observable.just(SearchResponse(1, searchResults))
        }
        val pickGameInteractor = PickGameInteractor(boardGameGeekService)

        val delayedTestObserver = pickGameInteractor.fetchSearchResults("firstQuery").test()
        delayedTestObserver.assertNoValues()
        pickGameInteractor.fetchSearchResults("secondQuery").test()
                .assertValue(PartialPickGameViewState.ResultsFetchedState(searchResults))
        delayedSearchResponseSubject.onNext(SearchResponse(1, delayedSearchResults))
        delayedTestObserver.assertNoValues()
    }

    @Test
    fun testResultsFetchingWithError() {
        val exception = Exception("testError")
        val boardGameGeekService: BoardGameGeekService = mock {
            on { it.search(any()) } doReturn Observable.error(exception)
        }
        val pickGameInteractor = PickGameInteractor(boardGameGeekService)

        pickGameInteractor.fetchSearchResults("testQuery").test()
                .assertValue(PartialPickGameViewState.ErrorState(exception, "testQuery"))
    }
}