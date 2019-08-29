package mockgps.search

import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.maps.model.LatLngBounds
import com.saketh.mockgps.arch.Event
import mockgps.core.Provider
import mockgps.core.ScopedViewModel
import mockgps.core.scope.Scope
import mockgps.model.Location
import mockgps.model.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import utils.INVALID_ID
import utils.copy
import java.util.*

class SearchViewModel(
    searchScope: Scope
) : ScopedViewModel(searchScope) {

  private val STYLE_BOLD = StyleSpan(Typeface.BOLD) as CharacterStyle
  private val STYLE_REGULAR = StyleSpan(Typeface.NORMAL) as CharacterStyle

  lateinit var repository: Repository
  lateinit var searchApiService: SearchApiService
  lateinit var selectedLocation: MutableLiveData<Event<Location>>
  lateinit var navigateUp: MutableLiveData<Event<Any>>
  lateinit var windowInsetTop: LiveData<Int>
  lateinit var windowInsetBottom: LiveData<Int>

  val _searching: MutableLiveData<SearchState> = MutableLiveData()
  val searching: LiveData<SearchState>
    get() = _searching

  val searchResults = MutableLiveData<List<Location>>()
  val savedLocations = MutableLiveData<List<Location>>()

  private val viewModelJob = Job()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  private var searchJob: Job = Job()

  override fun onInject(provider: Provider) {
    (provider as SearchProvider).let {
      repository = it.repository
      searchApiService = it.searchApiService
      selectedLocation  = it.selectedLocation
      navigateUp = MutableLiveData()
      windowInsetTop = it.windowInsetTop
      windowInsetBottom = it.windowInsetBottom
    }
  }

  override fun onDestroy() {
    Log.d("devsak", "SearchQueryViewModel: onCleared")
    searchApiService.onDestroy()
  }

  fun search(text: String, bounds: LatLngBounds? = null) {
    _searching.value = SearchInProgress(text)

    searchJob.cancel()
    searchJob = Job()

    CoroutineScope(Dispatchers.Main + searchJob).launch {
      Log.d("devsak", "${Thread.currentThread()} : search fetch awaiting")
      val result = searchApiService.search(text, bounds)
      Log.d("devsak", "${Thread.currentThread()} : search fetch complete")

      _searching.value = SearchComplete(text)

      val list = ArrayList<Location>()
      result?.forEach {
        Log.d("devsak", "${it.getPrimaryText(STYLE_REGULAR)} : ${it.getSecondaryText(STYLE_REGULAR)} : ${it.placeId}")
        list.add(toLocation(it))
      }
      searchResults.value = list
    }
  }

  fun cancelSearch(text: String) {
    searchJob.cancel()
    _searching.value = SearchCancelled(text)
  }

  fun onResultClick(searchResult: Location) {
    selectedLocation.value = Event(searchResult)
    navigateUp.value = Event(Any())
  }

  fun onSavedLocationClick(location: Location) {
    if (location.primaryName != null) {
      selectedLocation.value = Event(location)
    }
    navigateUp.value = Event(Any())
  }

  // FIXME
  fun onLocationSelected(location: Location) {

    // if location has latlng then no need to


    uiScope.launch {
      if (!location.hasLatLng()) {
        Log.d("devsak", "${Thread.currentThread()} : place search fetch awaiting")
        val result = searchApiService.getPlace(location.placeId)
        Log.d("devsak", "${Thread.currentThread()} : place search fetch complete")

        result?.let {
          val copy = location.copy(it.latLng.latitude.toString(), it.latLng.longitude.toString())
          repository.saveRecent(copy)
        }
      }
    }
  }

  private fun toLocation(prediction: AutocompletePrediction) = Location(id = INVALID_ID,
      placeId = prediction.placeId.toString(), primaryName = prediction.getPrimaryText(STYLE_REGULAR).toString(),
      secondaryName = prediction.getSecondaryText(STYLE_REGULAR).toString(), alias = null, latitude = null,
      longitude = null)

  fun loadTopSavedPlaces() {
    uiScope.launch {
      Log.d("devsak", "${Thread.currentThread()} : load top saved places awaiting")
      savedLocations.value = repository.loadSavedLocations()
      Log.d("devsak", "${Thread.currentThread()} : load top saved places complete with ${savedLocations.value?.size}")
    }
  }

  fun loadPreviousSearches() {
    uiScope.launch {
      searchResults.value = repository.getAllRecents()
    }
    /*searchResults.value = listOf(
        SearchResult("asdfghf", "Devaraja Mohalla, Shivarampet, Mysuru, Karnataka", "ChIJxfh3WQtwrzsRCmAe295zo8o"),
        SearchResult("asdfghjkl", "Shastri Nagar, Gaya, Bihar, India", "ChIJAQAAkPsq8zkR3M1wZm-gfCA"),
        SearchResult("ASDF", "Charai, Borla, Union Park, Chembur East, Mumbai, Maharashtra, India", "ChIJtZT98qjI5zsR2CJb33ip2bs"),
        SearchResult("Asdfg", "Achli Gate, Batala, Punjab, India", "ChIJA9JBZFbCGzkRYtB9Dbr8Nq8"),
        SearchResult("Asdf", "Planet Circle, Lancaster, CA, USA", "ChIJO4z1gUdbwoAR-f2RxXdJO70"),
        SearchResult("asdfghf", "Devaraja Mohalla, Shivarampet, Mysuru, Karnataka", "ChIJxfh3WQtwrzsRCmAe295zo8o"),
        SearchResult("asdfghjkl", "Shastri Nagar, Gaya, Bihar, India", "ChIJAQAAkPsq8zkR3M1wZm-gfCA"),
        SearchResult("ASDF", "Charai, Borla, Union Park, Chembur East, Mumbai, Maharashtra, India", "ChIJtZT98qjI5zsR2CJb33ip2bs"),
        SearchResult("Asdfg", "Achli Gate, Batala, Punjab, India", "ChIJA9JBZFbCGzkRYtB9Dbr8Nq8"),
        SearchResult("Asdf", "Planet Circle, Lancaster, CA, USA", "ChIJO4z1gUdbwoAR-f2RxXdJO70")
    )*/
  }
}

data class SearchResult(val primary: String, val secondary: String = "snu", val placeId: String)

sealed class SearchState
class SearchInProgress(val searchText: String) : SearchState()
class SearchComplete(val searchText: String) : SearchState()
class SearchCancelled(val searchText: String) : SearchState()

