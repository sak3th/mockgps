package mockgps.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recents")
data class Recent(

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "place_id")
    val placeId: String,

    @ColumnInfo(name = "primary_name")
    val primaryName: String?,

    @ColumnInfo(name = "secondary_name")
    val secondaryName: String?,

    val alias: String?,

    val latitude: String?,

    val longitude: String?

    // TODO accuracy, altitude?
) {

    fun getAliasOrName() =  if (alias.isNullOrEmpty()) primaryName else alias

    fun getDisplayLatLng() = "$latitude, $longitude"

    fun hasLatLng() = latitude != null && longitude != null
}