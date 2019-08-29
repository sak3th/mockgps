package mockgps.actions

import android.util.Log
import androidx.annotation.WorkerThread
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Tasks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ActionsApiService(
    private val geoDataClient: GeoDataClient,
    private val jobs: ArrayList<Job> =  ArrayList()
) {

  private val autoCompleteFilter: AutocompleteFilter = AutocompleteFilter.Builder()
      .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
      .build()
  private val API_TIMEOUT = 10L

  suspend fun search(query: String, bounds: LatLngBounds? = null): ArrayList<AutocompletePrediction>? {
    return withContext(Dispatchers.IO) {
      val results = geoDataClient.getAutocompletePredictions(query, bounds, autoCompleteFilter)
      var list: ArrayList<AutocompletePrediction>? = null

      try {
        Log.d("devsak", "${Thread.currentThread()} : search triggered")
        Tasks.await<AutocompletePredictionBufferResponse>(results, API_TIMEOUT, TimeUnit.SECONDS)

        val autocompletePredictions = results.result
        Log.d("devsak", "${Thread.currentThread()} Query completed. Received " + autocompletePredictions?.count + " predictions.")

        list = DataBufferUtils.freezeAndClose(autocompletePredictions)
      } catch (e: ExecutionException) {
        e.printStackTrace()
      } catch (e: InterruptedException) {
        e.printStackTrace()
      } catch (e: TimeoutException) {
        e.printStackTrace()
      } catch (e: RuntimeExecutionException) {
        Log.d("devsak", "Error getting autocomplete prediction API call", e)
      }
      return@withContext list
    }
  }

  @WorkerThread
  suspend fun getPlace(placeId: String): Place? {
    return withContext(Dispatchers.IO) {
      val results = geoDataClient.getPlaceById(placeId)
      var place: Place? = null
      try {
        Log.d("devsak", "${Thread.currentThread()} : place search triggered")
        Tasks.await<PlaceBufferResponse>(results, API_TIMEOUT, TimeUnit.SECONDS)

        val resp = results.result
        resp?.let {
          if (it.count > 0) {
            place = resp.get(0)?.freeze()
            Log.d("devsak","${Thread.currentThread()} : place query completed. Received ${place?.latLng} : ${place?.address}")
            resp.release()
          }
        }

      } catch (e: ExecutionException) {
        e.printStackTrace()
      } catch (e: InterruptedException) {
        e.printStackTrace()
      } catch (e: TimeoutException) {
        e.printStackTrace()
      } catch (e: RuntimeExecutionException) {
        Log.d("devsak", "Error getting autocomplete prediction API call", e)
      }
      return@withContext place
    }
  }

  fun onCleared() {
    Log.d("devsak", "ActionsApiService: onCleared")
    jobs.forEach {
      it.cancel()
    }
  }
}

