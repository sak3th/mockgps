package mockgps.core

import androidx.room.Database
import androidx.room.RoomDatabase
import mockgps.model.*

@Database(
    entities = [
      Place::class,
      Prediction::class,
      PlaceAlias::class,
      Location::class,
      Recent::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun searchDao(): SearchDao

  abstract fun placeDao(): PlaceDao

  abstract fun placeAliasDao(): PlaceAliasDao

  abstract fun locationDao(): LocationDao

  abstract fun recentDao(): RecentDao
}