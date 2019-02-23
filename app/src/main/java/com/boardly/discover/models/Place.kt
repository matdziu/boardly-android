package com.boardly.discover.models

data class Place(var id: String = "",
                 var adminId: String = "",
                 val name: String = "",
                 val description: String = "",
                 val imageUrl: String = "",
                 var locationName: String = "",
                 var placeLatitude: Double = 0.0,
                 var placeLongitude: Double = 0.0,
                 val phoneNumber: String = "",
                 val pageLink: String = "",
                 val collectionId: String = "") {

    fun toMap(): Map<String, Any> {
        return mapOf(
                "id" to id,
                "adminId" to adminId,
                "name" to name,
                "description" to description,
                "imageUrl" to imageUrl,
                "locationName" to locationName,
                "placeLatitude" to placeLatitude,
                "placeLongitude" to placeLongitude,
                "phoneNumber" to phoneNumber,
                "pageLink" to pageLink,
                "collectionId" to collectionId)
    }
}