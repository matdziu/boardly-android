package com.boardly.home.models

import com.boardly.filter.models.Filter

data class FilteredFetchData(val filter: Filter,
                             val userLocation: UserLocation,
                             val init: Boolean)