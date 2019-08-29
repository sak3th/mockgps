package mockgps.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.saketh.mockgps.arch.Event
import mockgps.core.Provider
import mockgps.model.Location
import mockgps.model.Repository


class SearchProvider (
    val repository: Repository,
    val searchApiService: SearchApiService,
    val selectedLocation: MutableLiveData<Event<Location>>,
    val windowInsetTop: LiveData<Int>,
    val windowInsetBottom: LiveData<Int>
) : Provider()