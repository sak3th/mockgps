package mockgps.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


object Converters {
  private val gson = Gson()

  @TypeConverter
  @JvmStatic
  fun stringToPlaceIdsList(data: String?): List<String> {
    if (data.isNullOrEmpty()) {
      return Collections.emptyList()
    }

    val listType = object : TypeToken<List<String>>() {}.type
    return gson.fromJson<List<String>>(data, listType)
  }

  @TypeConverter
  @JvmStatic
  fun placeIdsListToString(someObjects: List<String>): String {
    return gson.toJson(someObjects)
  }
}