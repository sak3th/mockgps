package mockgps.model

import androidx.room.*

@Dao
interface PlaceAliasDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg placeAliases: PlaceAlias)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertSavedPlaces(placeAliases: List<PlaceAlias>)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun createSavedPlaceIfNotExists(placeAlias: PlaceAlias): Long

  @Query("SELECT * FROM place_aliases WHERE id = :id")
  fun get(id: String): PlaceAlias

  @Query("SELECT * FROM place_aliases")
  fun getAll(): List<PlaceAlias>

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(vararg placeAlias: PlaceAlias)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updatePlaces(placeAliases: List<PlaceAlias>)

  @Delete
  fun delete(vararg placeAliases: PlaceAlias)

  // TODO
  // fun delete( WHERE place_id = :id)
}