package com.boardly.retrofit.gamesearch.models

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "items")
data class SearchResponse @JvmOverloads constructor(@field:Attribute var total: Int = 0,
                                                    @field:Attribute var termsofuse: String = "",
                                                    @field:Element var games: List<SearchResult> = arrayListOf())