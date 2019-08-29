package mockgps.model

import android.location.Location as NativeLocation
import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelLocation(
    val nativeLocation: NativeLocation,
    val id: Int?,
    val placeId: String,
    val primaryName: String?,
    val secondaryName: String?,
    val alias: String?
) : Parcelable