package com.boardly.pickgame

import com.boardly.retrofit.gameservice.models.SearchResult

@Deprecated("Can't use BGG API")
data class PickGameViewState(val progress: Boolean = false,
                             val searchResults: List<SearchResult> = listOf(),
                             val error: Throwable? = null,
                             val unacceptedQuery: String = "")