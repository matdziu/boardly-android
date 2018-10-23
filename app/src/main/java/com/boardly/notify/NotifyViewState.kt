package com.boardly.notify

data class NotifyViewState(val gameImageUrl: String = "",
                           val progress: Boolean = false,
                           val success: Boolean = false)