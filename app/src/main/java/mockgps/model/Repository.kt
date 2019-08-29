package mockgps.model

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import utils.toLocation
import utils.toRecent
import java.util.*

class Repository(
    private val placeDao: PlaceDao,
    private val placeAliasDao: PlaceAliasDao,
    private val locationDao: LocationDao,
    private val recentDao: RecentDao,
    private val searchDao: SearchDao
) {

  private val RECENTS_MAX_COUNT = 20

  private var lastLocation: Location? = null

  fun init() {
  }

  suspend fun isPresent(placeId: String): Boolean {
    return withContext(Dispatchers.IO) {
      val res = placeDao.get(placeId)
      Log.d("devsak", " isPresent : $res ")
      return@withContext res != null
    }
  }

  suspend fun savePlace(place: Place/*, placeAlias: PlaceAlias*/): Boolean {
    return withContext(Dispatchers.IO) {
      val res = placeDao.createPlaceIfNotExists(place)
      Log.d("devsak", "placeDao.createPlaceIfNotExists : $res")
      return@withContext res > 0
      /*val aliasRes = placeAliasDao.createSavedPlaceIfNotExists(placeAlias)
      Log.d("devsak", "placeAliasDao.createPlaceIfNotExists : $aliasRes")*/
    }
  }

  suspend fun deletePlace(place: Place/*, placeAlias: PlaceAlias*/): Unit {
    return withContext(Dispatchers.IO) {
      placeDao.delete(place)
    }
  }

  suspend fun loadSavedLocations(): List<Location> {
    return withContext(Dispatchers.IO) {
      Log.d("devsak", "${Thread.currentThread()} : load saved locations deferred start")
      val locations = locationDao.getAll()
      Log.d("devsak", "${Thread.currentThread()} : load saved locations deferred complete")
      locations
    }
  }

  suspend fun getLocation(placeId: String): Location? {
    return withContext(Dispatchers.IO) {
      val res = locationDao.get(placeId)
      Log.d("devsak", " isPresent : $res ")
      return@withContext res
    }
  }

 suspend fun saveLocation(location: Location): Boolean {
    return withContext(Dispatchers.IO) {
      var res = locationDao.createLocationIfNotExists(location)
      Log.d("devsak", "locationDao.createLocationIfNotExists : $res")
      if (res < 0) {
        res = locationDao.update(location).toLong()
      }
      return@withContext res > 0
      /*val aliasRes = placeAliasDao.createSavedPlaceIfNotExists(placeAlias)
      Log.d("devsak", "placeAliasDao.createPlaceIfNotExists : $aliasRes")*/
    }
  }

  suspend fun unsaveLocation(location: Location): Unit {
    return withContext(Dispatchers.IO) {
      locationDao.delete(location)
    }
  }

  suspend fun saveRecent(location: Location) {
    val recent = location.toRecent()
    withContext(Dispatchers.IO) {
      val result = recentDao.get(recent.placeId)
      result?.let {
        recentDao.delete(it)
      }
      recentDao.insert(recent)
      val all = recentDao.getAll()
      all.let {
        val overflow = it.size - RECENTS_MAX_COUNT
        if (overflow > 0) {
          val sortedRecents = it.sortedBy { it.id }
          for (delIndex in 0 until overflow) {
            recentDao.delete(sortedRecents[delIndex])
          }
        }
      }
    }
  }

  suspend fun getAllRecents(): List<Location> {
    return withContext(Dispatchers.IO) {
      val recents = recentDao.getAll()
      val list = ArrayList<Location>(recents.size)
      for (i in 0 until recents.size) {
        list.add(i, recents[i].toLocation())
      }
      return@withContext list
    }
  }

  private fun createSavedPlace(place: Place, alias: PlaceAlias): SavedPlace {
    val name = alias.alias ?: place.primaryName
    return SavedPlace(alias.id, name, place.id, place.latitude, place.longitude)
  }
}