package com.saketh.mockgps.map

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MapStyleOptions
import com.google.android.libraries.maps.model.Marker
import com.google.android.libraries.maps.model.MarkerOptions
import com.saketh.mockgps.R
import mockgps.core.BaseFragment
import mockgps.map.MapViewModel
import mockgps.model.Location
import timber.log.Timber

class MapFragment : BaseFragment() {

  private lateinit var viewModel: MapViewModel

  private lateinit var mapView: MapView
  private lateinit var map: GoogleMap
  private var mockMarker: Marker? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.map_fragment, container, false).apply {
      mapView = findViewById(R.id.mapView)
      mapView.onCreate(savedInstanceState)
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = getViewModel(MapViewModel::class.java)
    mapView.getMapAsync {
      onMapReady(it)
    }
  }

  override fun onResume() {
    mapView.onResume()
    super.onResume()
  }

  fun onMapReady(googleMap: GoogleMap) {
    Log.d("devsak", "onMapReady: ")
    map = googleMap

    map.isMyLocationEnabled = true //FIXME
    map.uiSettings.isCompassEnabled = true
    //map.uiSettings.isMapToolbarEnabled = true
    map.uiSettings.isZoomControlsEnabled = true
    map.uiSettings.isMyLocationButtonEnabled = true

    map.setOnMapClickListener { latLng ->
      viewModel.onMapClick(latLng)
    }
    viewModel.tappedLocation.observe(this, Observer<LatLng> {
      Log.d("devsak", "MapFragment: mocklocation observe")
      //it?.let { replaceMarker(it, animateCamera = true) }
    })
    viewModel.mockLocation.observe(this, Observer { optionalEvent ->
      Timber.d("MapFragment: locationEvent observe")
      optionalEvent?.let { event ->
        event.peekContent().let { location ->
          getLatLng(location)?.let { latLng ->
            replaceMarker(latLng)
          }
        }
      }
    })
    viewModel.bottomPadding.observe(this, Observer {
      val bottomPadding = it?.peekContent() ?: 0
      val topInset = viewModel.windowInsetTop.value ?: 0
      setPadding(bottomPadding, topInset, lifecycle)
    })

    //testDynamicMapStyle()
    testDayNightMapStyle()
  }

  private fun testDayNightMapStyle() {
    val mode = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
    when (mode) {
      Configuration.UI_MODE_NIGHT_YES -> loadDayNightStyle(map)
      Configuration.UI_MODE_NIGHT_NO -> loadDayMockStyle(map)
      Configuration.UI_MODE_NIGHT_UNDEFINED -> loadDayMockStyle(map)
    }
  }

  private fun loadDayMockStyle(map: GoogleMap) {
    loadMapStyle(map, R.raw.uber_maps_style)
  }

  private fun loadDayNightStyle(map: GoogleMap) {
    loadMapStyle(map, R.raw.night_maps_style)
  }

  private fun loadDesatStyle(map: GoogleMap) {
    loadMapStyle(map, R.raw.google_maps_desat_poi)
  }

  private fun loadMapStyle(googleMap: GoogleMap, mapsStyleResource: Int) {
    try {
      val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, mapsStyleResource))
      if (!success) {
        Log.e("devsak", "Style parsing failed.")
      }
    } catch (e: Resources.NotFoundException) {
      Log.e("devsak", "Can't find style. Error: ", e)
    }
  }

  fun replaceMarker(latLng: LatLng, animateCamera: Boolean = true) {
    val distance = mockMarker?.position?.distanceTo(latLng)
    mockMarker?.remove()
    latLng?.let {
      Log.d("devsak", "replaceMarker: ")
      mockMarker = map.addMarker(
          MarkerOptions()
              .position(it)
              .title("Tap to mock location")
              .snippet("and snippet")
              /*.icon(BitmapDescriptorFactory
                  .fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_marker_loc))
              )*/
      )
      if (animateCamera) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f), 600, null)
      } else if (animateCamera) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
      }
    }
  }

  fun setPadding(bottomPadding: Int, topInset: Int, lifecycle: Lifecycle) {
    val height = activity?.window?.decorView?.height ?: 0
    if (bottomPadding != 0 && height != 0
        && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
      map.setPadding(10, topInset - 4, 0, (height - bottomPadding) - topInset)
    }
  }

  fun getStatusBarHeight(): Int {
    return viewModel.windowInsetTop.value ?: 0
  }

  fun getLatLng(location: Location): LatLng? {
    if (location.latitude != null && location.longitude != null) {
      return LatLng(location.latitude?.toDouble(), location.longitude.toDouble())
    }
    return null
  }


  override fun onDestroy() {
    mapView.onDestroy()
    super.onDestroy()
  }

  override fun onLowMemory() {
    mapView.onLowMemory()
    super.onLowMemory()
  }

  fun LatLng.distanceTo(that: LatLng): Float {
    val earthRadius = 3958.75
    val latDiff = Math.toRadians((that.latitude - this.latitude))
    val lngDiff = Math.toRadians((that.longitude - this.longitude))
    val a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(that.latitude)) *
        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    val distance = earthRadius * c

    val meterConversion = 1609

    return (distance * meterConversion).toFloat()
  }
}