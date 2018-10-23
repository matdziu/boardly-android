package com.boardly.notify

import com.boardly.notify.models.NotifySettings

data class NotifyViewState(val gameImageUrl: String = "",
                           val notifySettings: NotifySettings = NotifySettings(),
                           val progress: Boolean = false,
                           val success: Boolean = false)