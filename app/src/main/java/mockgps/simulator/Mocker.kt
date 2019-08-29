package com.saketh.mockgps.simulator

import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import mockgps.model.ParcelLocation
import mockgps.simulator.EXTRA_PARCEL_LOC
import mockgps.simulator.INTENT_ACTION_MOCK_LOCATION

private val PROVIDERS = arrayOf(
    LocationManager.GPS_PROVIDER,
    //"fused",
    //"wifi",
    LocationManager.NETWORK_PROVIDER)
//private val PROVIDERS = arrayOf(LocationManager.GPS_PROVIDER)
private val DUMMY_PROVIDER = "dummy"

private const val DEFAULT_ACC = 1f

private const val MSG_SET_LOC_AND_REPEAT = 101
private const val KEY_LOCATION = "key_location"

private const val LOCATION_UPDATE_INTERVAL = 2000L

class Mocker(
    private val appContext: Context
) {

  private var mockParcelLocation: ParcelLocation? = null
  private var apiClient: FusedLocationProviderClient? = null

  init {
    Log.d("devsak", "created Mocker: ")
  }

  // FIXME get background thread looper
  // FIXME get rid of handler and move to thread
  private val handler = object : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message?) {
      when (msg?.what) {
        MSG_SET_LOC_AND_REPEAT -> {
          if (mockParcelLocation != null) {
            (msg.obj as Location).apply {
              setLocation(this.latitude, this.longitude, this.accuracy)
            }
            sendMessageDelayed(Message.obtain(msg), LOCATION_UPDATE_INTERVAL)
          } else {
            stop()
          }
        }
        else -> super.handleMessage(msg)
      }
    }
  }

  fun isMockable(): Boolean {
    return try {
      addProvider(DUMMY_PROVIDER)
      Log.d("devsak", "added dummy provider")
      locationMgr.setTestProviderEnabled("dummy",true)
      Log.d("devsak", "dummy provider enabled")
      locationMgr.clearTestProviderEnabled("dummy")
      Log.d("devsak", "dummy provider cleared")
      locationMgr.removeTestProvider("dummy")
      Log.d("devsak", "dummy provider removed")
      Log.d("devsak", "isMockable: true")
      true
    } catch (e: Exception) {
      Log.d("devsak", "isMockable: false $e" )
      false
    }
  }

  fun start(loc: ParcelLocation) {
    mockParcelLocation = loc

    addProviders(PROVIDERS)
    enableProviders(PROVIDERS)
    setLocation(loc.nativeLocation.latitude, loc.nativeLocation.longitude)
    schedulePeriodicUpdates(loc.nativeLocation.latitude, loc.nativeLocation.longitude)
    broadcastMockLoc()
  }

  fun stop(vararg provider: String = PROVIDERS) {
    Log.d("devsak", "stop mock: ")
    handler.removeMessages(MSG_SET_LOC_AND_REPEAT)
    handler.removeCallbacksAndMessages(null)

    apiClient?.setMockMode(false)

    clearLocation(PROVIDERS)
    clearProviders(PROVIDERS)
    removeProviders(PROVIDERS)
    mockParcelLocation = null
  }

  fun broadcastMockLoc() {
    val intent = Intent(INTENT_ACTION_MOCK_LOCATION)
    if (mockParcelLocation != null) {
      intent.putExtra(EXTRA_PARCEL_LOC, mockParcelLocation)
    }
    LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent)
  }

  private fun schedulePeriodicUpdates(lat: Double, lng: Double, acc: Float = DEFAULT_ACC) {
    val firstMsg = handler.obtainMessage(MSG_SET_LOC_AND_REPEAT, Location("dummy").also {
      it.latitude = lat
      it.longitude = lng
      it.accuracy = acc
    })
    firstMsg.sendToTarget()
  }

  private fun addProviders(providers: Array<out String>) {
    providers.forEach {
      addProvider(it)
    }
  }

  private fun addProvider(provider: String) {
    try {
      locationMgr.addTestProvider(
          provider,
          true,
          true,
          true,
          false,
          true,
          true,
          true,
          Criteria.POWER_LOW,
          Criteria.ACCURACY_HIGH
      )
    } catch (t: Throwable) {
      Log.e("devsak", "Exception while adding provider $provider : $t")
    }
  }

  private fun removeProviders(providers: Array<out String>) {
    providers.forEach {
      locationMgr.removeTestProvider(it)
    }
  }


  private fun enableProviders(providers: Array<out String>) {
    providers.forEach {
      locationMgr.setTestProviderEnabled(it,true)
      locationMgr.setTestProviderStatus(it, LocationProvider.AVAILABLE, null, System.currentTimeMillis())
    }
  }

  private fun clearProviders(providers: Array<out String>) {
    providers.forEach {
      locationMgr.clearTestProviderEnabled(it)
      locationMgr.clearTestProviderStatus(it)
    }
  }

  private fun setLocation(lat: Double, lng: Double, acc: Float = DEFAULT_ACC) {
    Log.d("devsak", "setLocation: ")
    apiClient ?: run {
      apiClient = LocationServices.getFusedLocationProviderClient(appContext)
    }

    apiClient?.setMockMode(true)

    PROVIDERS.forEach {
      val location = createLocation(lat, lng, acc, it)
      locationMgr.setTestProviderLocation(it, location)
      apiClient?.setMockLocation(location)
      Log.d("devsak", "setTestProviderLocation ${it} : $lat,$lng : ${Thread.currentThread()}")
    }
  }

  private fun createLocation(lat: Double, lng: Double, acc: Float = DEFAULT_ACC, provider: String) =
      Location(provider).also {
        it.latitude = lat
        it.longitude = lng
        it.accuracy = acc
        it.altitude = 1.0
        it.bearing = 1.0f
        it.time = System.currentTimeMillis()
        it.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        isComplete(it)
      }

  private fun clearLocation(providers: Array<out String>) {
    providers.forEach {
      locationMgr.clearTestProviderLocation(it)
    }
  }

  private val locationMgr by lazy {
    appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
  }

  fun isComplete(loc: Location): Boolean {
    if (loc.provider == null) {
      Log.d("devsak", "provider is null")
      return false
    }
    if (!loc.hasAccuracy()) {
      Log.d("devsak", "accuracy not set")
      return false
    }
    if (loc.time == 0L) {
      Log.d("devsak", "time not set")
      return false
    }
    return if (loc.elapsedRealtimeNanos == 2000L) {
      Log.d("devsak", "elapsedRealtimeNanos not set")
      false
    } else true
  }
}