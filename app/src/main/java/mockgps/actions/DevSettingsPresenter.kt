package mockgps.actions

import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class DevSettingsPresenter(
    private val view: View,
    private val mockable: LiveData<Boolean>,
    private val lifecycleOwner: LifecycleOwner) {

  init {
    view.apply {
      visibility = View.GONE
      setOnClickListener { openDevSettings() }
    }
  }

  fun load() {
    mockable.apply {
      removeObservers(lifecycleOwner)
      observe(lifecycleOwner, Observer {
        Log.d("devsak", "mockable : ${it}")
        view.visibility = if (it == true) View.GONE else View.VISIBLE
      })
    }
  }

  private fun openDevSettings() {
    Toast.makeText(view.context, "Open Dev Settings", Toast.LENGTH_SHORT).show()
    view.context.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
  }
}