package com.boardly.common.players.models

data class Player(var id: String = "",
                  val name: String = "",
                  val profilePicture: String = "",
                  val rating: Double? = null,
                  var helloText: String = "",
                  var ratedOrSelf: Boolean = false)