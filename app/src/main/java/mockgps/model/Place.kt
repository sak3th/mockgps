package mockgps.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class Place(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "primary_name")
    val primaryName: String,

    @ColumnInfo(name = "secondary_name")
    val secondaryName: String,

    val latitude: String,

    val longitude: String
)