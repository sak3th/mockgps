package mockgps.search

import mockgps.model.SearchDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository(
    private val searchDao: SearchDao,
    private val searchApiService: SearchApiService
) {

  suspend fun get(searchKey: String): List<String> {
    return withContext(Dispatchers.IO) {
      searchDao.get(searchKey)
    }
  }
}