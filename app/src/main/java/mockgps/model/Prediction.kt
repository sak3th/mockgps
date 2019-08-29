package mockgps.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "predictions")
@TypeConverters(Converters::class)
data class Prediction(
    @PrimaryKey
    val searchKey: String,

    @ColumnInfo(name = "place_ids")
    val placeIds: List<String>
) {

}