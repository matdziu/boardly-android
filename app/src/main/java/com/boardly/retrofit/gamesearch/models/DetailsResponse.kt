package com.boardly.retrofit.gamesearch.models

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "items")
data class DetailsResponse @JvmOverloads constructor(@field:Element var game: Game = Game())
