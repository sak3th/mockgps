package mockgps.core

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import mockgps.Config
import com.saketh.mockgps.arch.Event
import mockgps.search.SearchApiService
import mockgps.simulator.MockLocationDelegate
import com.saketh.mockgps.simulator.Mocker
import mockgps.model.*

class AppProvider(val appContext: Context) : Provider() {

  val geoDataClient: GeoDataClient by lazy {
    Places.getGeoDataClient(appContext)
  }

  val searchApiService: SearchApiService by lazy {
    SearchApiService(geoDataClient)
  }

  val mockLocationDelegate: MockLocationDelegate by lazy {
    MockLocationDelegate(appContext)
  }

  val paddingBottom = MutableLiveData<Event<Int>>()

  val db: AppDatabase by lazy {
      //Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
      Room.databaseBuilder(appContext, AppDatabase::class.java, "mockgps")
          .build()
  }
  val searchDao: SearchDao by lazy {
    db.searchDao()
  }
  val placeDao: PlaceDao by lazy {
    db.placeDao()
  }
  val placeAliasDao: PlaceAliasDao by lazy {
    db.placeAliasDao()
  }
  val locationDao: LocationDao by lazy {
    db.locationDao()
  }
  val recentDao: RecentDao by lazy {
    db.recentDao()
  }
  val repository: Repository by lazy {
    Repository(placeDao, placeAliasDao, locationDao, recentDao, searchDao)
  }

  val config: Config by lazy {
    Config()
  }
  val mocker by lazy {
    Mocker(appContext)
  }

}