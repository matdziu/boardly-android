package com.boardly.common.search

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResultData(val id: String,
                            val title: String,
                            val subtitle: String,
                            val additionalInfo: Map<String, String> = hashMapOf()) : Parcelable