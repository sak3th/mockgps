package mockgps.map

import android.os.Bundle
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.saketh.mockgps.R
import mockgps.core.BaseAppCompatActivity

class MapsActivity : BaseAppCompatActivity(), OnMapReadyCallback {

  private lateinit var map: GoogleMap

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_maps)
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    val mapFragment = supportFragmentManager
        .findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
  }


  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap

    // Add a marker in Sydney and move the camera
    val sydney = LatLng(-34.0, 151.0)
    map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
    map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
  }
}
