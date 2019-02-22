package com.boardly.gamescollection

import com.boardly.gamescollection.models.CollectionGame

data class GamesCollectionViewState(val progress: Boolean = false,
                                    val games: List<CollectionGame> = listOf(),
                                    val success: Boolean = false)