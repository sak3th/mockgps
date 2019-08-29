package mockgps.actions

import android.content.Context
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialog

class SaveLocationDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme) {
  init {
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
  }
}