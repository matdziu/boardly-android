package com.boardly.retrofit.gamesearch.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList

data class SearchResponse(@field:Attribute var total: Int = 0,
                          @field:Attribute var termsofuse: String = "",
                          @field:ElementList(name = "items", inline = true, required = false)
                          var games: ArrayList<SearchResult> = arrayListOf())