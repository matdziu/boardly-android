package com.boardly.manageplace.models

import com.boardly.discover.models.Place
import java.io.File

data class PlaceInputData(val place: Place = Place(), val imageFile: File? = null)