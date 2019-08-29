package utils

import android.os.SystemClock
import android.location.Location as NativeLocation
import com.google.android.libraries.maps.model.LatLng
import mockgps.model.Location
import mockgps.model.ParcelLocation
import mockgps.model.Recent
import mockgps.search.SearchResult
import java.util.*

val INVALID_ID: Int? = null


fun Location.toLocation(loc: Location, id: Int?, placeId: String) = Location(
    INVALID_ID, placeId, null, null, null, this.latitude.toString(), this.longitude.toString())

fun LatLng.toLocation(placeId: String, primaryName: String) = Location(INVALID_ID, placeId, primaryName,
    null, null, this.latitude.toString(), this.longitude.toString())

fun SearchResult.toLocation() = Location(INVALID_ID, this.placeId, this.primary,
    this.secondary, null, null, null)

fun SearchResult.toLocation(latLng: LatLng) = Location(INVALID_ID, this.placeId,
    this.primary, this.secondary, null, latLng.latitude.toString(), latLng.longitude.toString())

fun ParcelLocation.toLocation() = Location(this.id, this.placeId, this.primaryName,
    this.secondaryName, this.alias, this.nativeLocation.latitude.toString(), this.nativeLocation.longitude.toString())

fun Location.toParcelLocation(acc: Float = 1f) = ParcelLocation(
    NativeLocation("dummy").apply {
      this@toParcelLocation.latitude?.toDouble()?.let { this.latitude = it }
      this@toParcelLocation.longitude?.toDouble()?.let { this.longitude = it }
      this.accuracy = acc
      this.altitude = 1.0
      this.bearing = 1.0f
      this.time = System.currentTimeMillis()
      this.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
    }, this.id, this.placeId, this.primaryName, this.secondaryName, this.alias)

fun Location.copy(latitude: String, longitude: String) = Location(this.id, this.placeId,
    this.primaryName, this.secondaryName, this.alias, latitude, longitude)

fun Location.copy(latLng: LatLng) = Location(this.id, this.placeId, this.primaryName,
    this.secondaryName, this.alias, latLng.latitude.toString(), latLng.longitude.toString())

fun Location.copy(alias: String) = Location(this.id, this.placeId, this.primaryName,
    this.secondaryName, alias, this.latitude, this.longitude)

fun Location.toRecent() = Recent(this.id, this.placeId, this.primaryName,
    this.secondaryName, this.alias, this.latitude, this.longitude)

fun Recent.toLocation() = Location(this.id, this.placeId, this.primaryName,
    this.secondaryName, this.alias, this.latitude, this.longitude)

fun String.isUuid(): Boolean {
  try {
    UUID.fromString(this)
    return true
  } catch (e: Exception) {
    return false
  }
}