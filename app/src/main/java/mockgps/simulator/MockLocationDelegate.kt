package mockgps.simulator

import android.content.Context
import mockgps.model.ParcelLocation

class MockLocationDelegate(
    private val appContext: Context
) {

  fun checkMock() {
    MockLocationService.checkMock(appContext);
  }

  fun startMock(loc: ParcelLocation) {
    MockLocationService.startMock(appContext, loc)
  }

  fun stopMock() {
    MockLocationService.stopMock(appContext)
  }

  fun broadcastMockLocation() {
    MockLocationService.broadcastMockLocation(appContext)
  }
}