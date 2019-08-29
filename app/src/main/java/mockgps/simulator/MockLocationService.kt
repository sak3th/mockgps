package mockgps.simulator

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import mockgps.HomeActivity
import com.saketh.mockgps.R
import mockgps.core.App
import mockgps.model.ParcelLocation

const val INTENT_ACTION_MOCK_LOCATION = "mockgps.intent.action.mock_loc"

const val ACTION_CHECK_MOCK = "mockgps.action.check_mock"
const val ACTION_START_MOCK = "mockgps.action.start_mock"
const val ACTION_STOP_MOCK = "mockgps.action.stop_mock"
const val ACTION_UPDATE_LOC = "mockgps.action.set_loc"
const val ACTION_BROADCAST_MOCK_LOCATION = "mockgps.action.broadcast_mock_location"

const val EXTRA_LOC = "mockgps.extra.loc"
const val EXTRA_PARCEL_LOC = "mockgps.extra.parcel_loc"
const val EXTRA_IS_MOCKING = "mockgps.extra.is_mocking"

private const val MOCK_NOTIFICATION_ID = 1


class MockLocationService : Service() {

  private val config by lazy {
    (applicationContext as App).getConfig()
  }

  private val mocker by lazy {
    (applicationContext as App).getMocker()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d("devsak", "onStartCommand: ")
    val loc = intent?.getParcelableExtra<ParcelLocation>(EXTRA_PARCEL_LOC)

    when (intent?.action) {
      ACTION_CHECK_MOCK -> config.mockable.value = mocker.isMockable()
      ACTION_STOP_MOCK -> handleActionStopMock()
      ACTION_START_MOCK -> handleActionStartMock(loc)
      ACTION_UPDATE_LOC -> handleActionUpdateLoc(loc)
      ACTION_BROADCAST_MOCK_LOCATION -> handleActionBroadcastMockLoc()
    }
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private fun handleActionStartMock(loc: ParcelLocation?){
    requireNotNull(loc) { "Location cannot be null" }
    check(mocker.isMockable()) { "Unable to set app as mockable" }

    startServiceInForeground()
    mocker.start(loc)
  }

  private fun handleActionStopMock() {
    stopForeground(true)
    mocker.stop()
  }

  private fun handleActionUpdateLoc(loc: ParcelLocation?) {
    requireNotNull(loc, { "Location cannot be null" })
    check(mocker.isMockable(), { "Unable to set app as mockable" })
    TODO("Handle action update loc")
  }

  private fun handleActionBroadcastMockLoc() {
    mocker.broadcastMockLoc()
  }

  private fun startServiceInForeground() {
    createChannelForOreoAndAbove()

    val target = Intent(this, HomeActivity::class.java)
    val content =
        PendingIntent.getActivity(this, 0, target, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(this, "Service")
        .setSmallIcon(R.mipmap.ic_launcher_white)
        .setContentTitle("MockGps")
        .setTicker("Mocking location at TODO Place")
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentText("Mocking location at TODO Place")
        .setContentIntent(content)
        .setChannelId("service")
        .setOngoing(true)

    /*val notification = builder.build()
    notification.flags = Notification.FLAG_FOREGROUND_SERVICE
    startForeground(1, notification)*/


    val pendingIntent: PendingIntent =
        Intent(this, HomeActivity::class.java).let { notificationIntent ->
          PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

    val notification = NotificationCompat.Builder(this, "Service")
        .setContentTitle(getText(R.string.app_name))
        .setContentText("Mocking location at place - TODO")
        .setSmallIcon(R.mipmap.ic_launcher_white)
        .setContentIntent(pendingIntent)
        .setTicker("Mocking location at place - TODO")
        .setChannelId("service")
        .build()

    //notification.flags = Notification.FLAG_FOREGROUND_SERVICE
    startForeground(MOCK_NOTIFICATION_ID, notification)
  }

  private fun createChannelForOreoAndAbove() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return
    }

    val channel = "Mock location service";
    val notiChannel =
        NotificationChannel("service", channel, NotificationManager.IMPORTANCE_LOW).apply {
          lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
    val notiMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notiMgr.createNotificationChannel(notiChannel)
  }

  companion object {
    @JvmStatic
    fun checkMock(context: Context) {
      val intent = Intent(context, MockLocationService::class.java).apply {
        action = ACTION_CHECK_MOCK
      }
      context.startService(intent)
    }

    @JvmStatic
    fun startMock(context: Context, loc: ParcelLocation) {
      val intent = Intent(context, MockLocationService::class.java).apply {
        action = ACTION_START_MOCK
        putExtra(EXTRA_PARCEL_LOC, loc)
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    @JvmStatic
    fun stopMock(context: Context) {
      val intent = Intent(context, MockLocationService::class.java).apply {
        action = ACTION_STOP_MOCK
      }
      context.startService(intent)
    }

    @JvmStatic
    fun updateLocation(context: Context, loc: Location) {
      val intent = Intent(context, MockLocationService::class.java).apply {
        action = ACTION_UPDATE_LOC
        putExtra(EXTRA_LOC, loc)
      }
      context.startService(intent)
    }

    @JvmStatic
    fun broadcastMockLocation(context: Context) {
      val intent = Intent(context, MockLocationService::class.java).apply {
        action = ACTION_BROADCAST_MOCK_LOCATION
      }
      context.startService(intent)
    }
  }
}
