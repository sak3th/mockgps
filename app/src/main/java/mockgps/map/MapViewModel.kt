package mockgps.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.maps.model.LatLng
import com.saketh.mockgps.arch.Event
import mockgps.core.Provider
import mockgps.core.ScopedViewModel
import mockgps.core.scope.Scope
import mockgps.model.Location

class MapViewModel(
    mapScope: Scope
) : ScopedViewModel(mapScope) {

  lateinit var tappedLocation: MutableLiveData<LatLng>
  lateinit var tappedLocationEvent: MutableLiveData<Event<LatLng>>
  lateinit var mockLocation: LiveData<Event<Location>>
  lateinit var bottomPadding: LiveData<Event<Int>>
  lateinit var windowInsetTop: LiveData<Int>
  lateinit var windowInsetBottom: LiveData<Int>

  override fun onInject(provider: Provider) {
    (provider as MapProvider).let {
      tappedLocation = it.tappedLocation
      tappedLocationEvent = it.mapTappedLatLng
      mockLocation = it.mockLocation
      bottomPadding = it.paddingBottom
      windowInsetTop = it.windowInsetTop
      windowInsetBottom = it.windowInsetBottom
    }
  }

  init {
  }

  fun onMapClick(latLng: LatLng) {
    tappedLocation.value = latLng
    tappedLocationEvent.value = Event(latLng)
  }

  override fun onDestroy() {
    Log.d("devsak", "MapViewModel: onCleared")
  }
}