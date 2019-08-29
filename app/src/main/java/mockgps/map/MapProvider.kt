package mockgps.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.maps.model.LatLng
import com.saketh.mockgps.arch.Event
import mockgps.core.Provider
import mockgps.model.Location

class MapProvider(
    val windowInsetTop: LiveData<Int>,
    val windowInsetBottom: LiveData<Int>
) : Provider()  {

  val tappedLocation = MutableLiveData<LatLng>()
  val paddingBottom = MutableLiveData<Event<Int>>()

  val mockLocation = MutableLiveData<Event<Location>>()

  val selectedLocation = MutableLiveData<Event<Location>>()
  val mapTappedLatLng = MutableLiveData<Event<LatLng>>()

}