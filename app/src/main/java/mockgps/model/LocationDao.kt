package mockgps.model

import androidx.room.*


@Dao
interface LocationDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg location: Location): Array<Long>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertLocations(locations: List<Location>): Array<Long>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun createLocationIfNotExists(location: Location): Long

  @Query("SELECT * FROM locations WHERE place_id = :id")
  fun get(id: String): Location?

  @Query("SELECT * FROM locations")
  fun getAll(): List<Location>

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(vararg location: Location): Int

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateLocations(locations: List<Location>)

  @Delete
  fun delete(vararg locations: Location)

  // TODO
  //fun deleteAll()
}
