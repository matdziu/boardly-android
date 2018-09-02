package com.boardly.constants

import com.boardly.R

const val USERS_NODE = "users"
const val EVENTS_NODE = "events"
const val PLAYERS_NODE = "players"
const val PENDING_EVENTS_NODE = "pending"
const val ACCEPTED_EVENTS_NODE = "accepted"
const val CREATED_EVENTS_NODE = "created"
const val CHATS_NODE = "chats"

const val NAME_CHILD = "name"
const val PROFILE_PICTURE_CHILD = "profilePicture"
const val RATING_HASHES = "ratingHashes"
const val TIMESTAMP_CHILD = "timestamp"
const val NOTIFICATION_TOKEN_CHILD = "notificationToken"

val LEVEL_IDS_MAP = mapOf(
        R.string.beginner_level to "1",
        R.string.intermediate_level to "2",
        R.string.advanced_level to "3")

val LEVEL_STRINGS_MAP = mapOf(
        "1" to R.string.beginner_level,
        "2" to R.string.intermediate_level,
        "3" to R.string.advanced_level)