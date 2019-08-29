package mockgps.model

import androidx.room.*


@Dao
interface RecentDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg recent: Recent): Array<Long>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun createRecentIfNotExists(recent: Recent): Long

  @Query("SELECT * FROM recents WHERE place_id = :id")
  fun get(id: String): Recent?

  @Query("SELECT * FROM recents")
  fun getAll(): List<Recent>

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(vararg recent: Recent): Int

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateRecents(recents: List<Recent>)

  @Delete
  fun delete(vararg recents: Recent)

  // FIXME
  @Query("DELETE FROM recents WHERE id NOT IN (SELECT id FROM recents ORDER BY id DESC LIMIT 2)")
  fun purgeUnused()

  // TODO
  //fun deleteAll()
}
