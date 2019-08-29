package com.saketh.mockgps.actions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.libraries.maps.model.LatLng
import com.saketh.mockgps.arch.Event
import mockgps.core.Provider
import mockgps.core.ScopedViewModel
import mockgps.core.scope.Scope
import mockgps.model.Location
import mockgps.model.Repository
import mockgps.simulator.MockLocationDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mockgps.actions.ActionsApiService
import mockgps.actions.ActionsProvider
import utils.copy
import utils.toLocation
import utils.toParcelLocation
import java.util.*

class ActionsViewModel(actionsScope: Scope) : ScopedViewModel(actionsScope) {

  val INVALID_ID: Int? = null

  private lateinit var mockLocation: MutableLiveData<Location>
  fun mockLocation() = mockLocation as LiveData<Location>

  lateinit var repository: Repository
  lateinit var actionsApiService: ActionsApiService
  lateinit var mockLocationDelegate: MockLocationDelegate

  lateinit var paddingBottom: MutableLiveData<Event<Int>>
  lateinit var locationEvent: MutableLiveData<Event<Location>>
  lateinit var windowInsetTop: LiveData<Int>
  lateinit var windowInsetBottom: LiveData<Int>
  lateinit var mockable: LiveData<Boolean>

  private lateinit var selectedLocation: LiveData<Event<Location>>
  private lateinit var mapTappedLatLng: LiveData<Event<LatLng>>

  private lateinit var searchedResultObserver: Observer<Event<Location>>
  private lateinit var mapTappedObserver: Observer<Event<LatLng>>

  private val viewModelJob = Job()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  override fun onInject(provider: Provider) {
    (provider as ActionsProvider).let {
      repository = it.repository
      actionsApiService = it.actionsApiService
      mockLocationDelegate = it.mockLocationDelegate

      selectedLocation = it.selectedLocation
      searchedResultObserver = Observer { onLocationSelected(it) }
      selectedLocation.observeForever(searchedResultObserver)

      mapTappedLatLng = it.mapTappedLatLng
      mapTappedObserver = Observer { onMapTappedLatLngChanged(it) }
      mapTappedLatLng.observeForever(mapTappedObserver)

      locationEvent = it.mockLocation

      paddingBottom = it.paddingBottom
      windowInsetTop = it.windowInsetTop
      windowInsetBottom = it.windowInsetBottom

      mockable = it.mockable
      mockLocation = MutableLiveData()
      mockLocationDelegate.broadcastMockLocation()
    }
  }

  fun load() { // FIXME
    /*repository.lastLocation?.let {
      mockLocation.value = it
    }*/
    if (mockLocation.value == null) {

    }
  }

  override fun onDestroy() {
    Log.d("devsak", "ActionsViewModel : onDestroy")
    selectedLocation.removeObserver(searchedResultObserver)
    mapTappedLatLng.removeObserver(mapTappedObserver)

    actionsApiService.onCleared()
  }

  private fun onMapTappedLatLngChanged(t: Event<LatLng>?) {
    t?.getContentIfNotHandled()?.let {
      Log.d("devsak", "onMapTappedLatLngChanged: ")
      locationEvent.value = Event(it.toLocation(UUID.randomUUID().toString(), "Pin Location"))
    }
  }

  private fun onLocationSelected(t: Event<Location>?) {
    t?.getContentIfNotHandled()?.let {
      uiScope.launch {
        var location = it
        Log.d("devsak",
            "onResultClick: ${location.primaryName}") //locationEvent.value = Event(location)
        if (!location.hasLatLng()) {
          Log.d("devsak", "${Thread.currentThread()} : place search fetch awaiting")
          val result = actionsApiService.getPlace(it.placeId)
          Log.d("devsak", "${Thread.currentThread()} : place search fetch complete")

          result?.let {
            location = location.copy(it.latLng.latitude.toString(), it.latLng.longitude.toString())
            repository.saveRecent(location)
          }
        }
        val repoLocation = repository.getLocation(location.placeId)
        locationEvent.value = Event(repoLocation ?: location)
      }
    }
  }

  fun onMockerStarted(loc: Location) {
    mockLocation.value = loc
  }

  fun startMock(location: Location) {
    mockLocationDelegate.startMock(location.toParcelLocation())
  }

  fun stopMock() {
    mockLocation.value = null
    mockLocationDelegate.stopMock()
  }

  fun save(loc: Location) {
    uiScope.launch {
      if (repository.saveLocation(loc)) {
        repository.getLocation(loc.placeId)?.let {
          locationEvent.value = Event(it)
        } ?: run {
          Log.e("devsak", "could not retrieve saved location ")
        }
      }
    }
  }

  fun unsave(loc: Location) {
    uiScope.launch {
      locationEvent.value?.let {
        repository.unsaveLocation(loc)
        locationEvent.value = Event(deleteLocation(loc))
      }
    }
  }

  fun saveRecent(loc: Location) {
    uiScope.launch {
      repository.saveRecent(loc)
    }
  }

  private fun deleteLocation(loc: Location): Location = Location(INVALID_ID,
      loc.placeId, loc.primaryName, loc.secondaryName, null, loc.latitude, loc.longitude)
}
