package com.boardly.retrofit.gamesearch.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "item")
data class SearchResult(@field:Attribute var type: String = "",

                        @field:Attribute var id: Int = 0,

                        @field:Path("name")
                        @field:Attribute(name = "value")
                        var name: String = "",

                        @field:Path("name")
                        @field:Attribute(name = "type")
                        var nameType: String = "",

                        @field:Path("yearpublished")
                        @field:Attribute(name = "value")
                        var yearPublished: String = "")
