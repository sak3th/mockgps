package mockgps.model

import androidx.room.*


@Dao
interface SearchDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg predictions: Prediction)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(predictions: List<Prediction>)

  @Query("SELECT place_ids FROM predictions WHERE searchKey = :searchKey")
  fun get(searchKey: String): List<String>

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(vararg predictions: Prediction)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(predictions: List<Prediction>)

  // TODO
  /*@Delete
  fun delete(vararg searchKeys: List<String>)*/

}