package mockgps.actions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.maps.model.LatLng
import com.saketh.mockgps.arch.Event
import mockgps.core.Provider
import mockgps.model.Location
import mockgps.model.Repository
import mockgps.simulator.MockLocationDelegate

class ActionsProvider(
    val repository: Repository,
    val actionsApiService: ActionsApiService,
    val mockLocationDelegate: MockLocationDelegate,
    val paddingBottom: MutableLiveData<Event<Int>>,
    val selectedLocation: LiveData<Event<Location>>,
    val mapTappedLatLng: LiveData<Event<LatLng>>,
    val mockLocation: MutableLiveData<Event<Location>>,
    val windowInsetTop: LiveData<Int>,
    val windowInsetBottom: LiveData<Int>,
    val mockable: LiveData<Boolean>
) : Provider()