package mockgps.model


data class SavedPlace(
    val id: Int,
    val name: String,
    val placeId: String,
    val latitude: String,
    val longitude: String
)