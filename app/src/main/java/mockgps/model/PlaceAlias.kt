package mockgps.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "place_aliases",
        foreignKeys = [ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["place_id"],
            onDelete = ForeignKey.CASCADE)]
)
data class PlaceAlias (
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val alias: String?,

    @ColumnInfo(name = "place_id")
    val placeId: String
)

