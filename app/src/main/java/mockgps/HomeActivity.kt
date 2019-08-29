package mockgps

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import com.saketh.mockgps.R
import mockgps.core.BaseAppCompatActivity

class HomeActivity : BaseAppCompatActivity() {

  private lateinit var viewModel: HomeViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)

    val view = findViewById<View>(android.R.id.content).apply {
      systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)/*
          or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)*/
    }
    
    viewModel = getViewModel(HomeViewModel::class.java)

    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
      Log.d("devsak", "activity compat listner: ")
      Log.d("devsak", "systemWindowInsetTop : ${insets.systemWindowInsetTop}")
      Log.d("devsak", "systemWindowInsetBottom : ${insets.systemWindowInsetBottom}")
      Log.d("devsak", "systemWindowInsetLeft : ${insets.systemWindowInsetLeft}")
      Log.d("devsak", "systemWindowInsetRight : ${insets.systemWindowInsetRight}")
      viewModel.windowInsetTop.value = insets.systemWindowInsetTop
      viewModel.windowInsetBottom.value = insets.systemWindowInsetBottom
      ViewCompat.setOnApplyWindowInsetsListener(view, null)
      insets?.consumeStableInsets()!!
    }
  }

  override fun onSupportNavigateUp() = findNavController(R.id.container).navigateUp()
}
