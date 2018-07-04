package com.boardly.retrofit.gamesearch.models

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "item")
data class SearchResult @JvmOverloads constructor(@field:Attribute var id: Int = 0,

                                                  @field:Path("name")
                                                  @field:Attribute(name = "value")
                                                  var name: String = "",

                                                  @field:Path("yearpublished")
                                                  @field:Attribute(name = "value")
                                                  var yearPublished: String = "")
