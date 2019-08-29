package permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class PermissionService(val appContext: Context) {

  fun checkPermission(permission: String) = ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED

}