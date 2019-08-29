package mockgps.model

import androidx.room.*


@Dao
interface PlaceDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg places: Place)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertPlaces(places: List<Place>)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun createPlaceIfNotExists(place: Place): Long

  @Query("SELECT * FROM places WHERE id = :id")
  fun get(id: String): Place?

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(vararg place: Place)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updatePlaces(places: List<Place>)

  @Delete
  fun delete(vararg places: Place)

  // TODO
  //fun deleteAll()
}
