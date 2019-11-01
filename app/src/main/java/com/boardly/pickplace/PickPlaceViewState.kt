package com.boardly.pickplace

import com.boardly.common.search.SearchResultData

data class PickPlaceViewState(val progress: Boolean = false,
                              val searchResults: List<SearchResultData> = listOf(),
                              val error: Throwable? = null,
                              val unacceptedQuery: String = "")