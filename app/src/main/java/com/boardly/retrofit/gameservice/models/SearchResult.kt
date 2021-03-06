package com.boardly.retrofit.gameservice.models

import android.os.Parcelable
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml
import kotlinx.android.parcel.Parcelize

@Parcelize
@Xml(name = "item")
data class SearchResult @JvmOverloads constructor(@field:Attribute var id: Int = 0,

                                                  @field:Attribute var type: String = "",

                                                  @field:Path("name")
                                                  @field:Attribute(name = "value")
                                                  var name: String = "",

                                                  @field:Path("yearpublished")
                                                  @field:Attribute(name = "value")
                                                  var yearPublished: String = "") : Parcelable
