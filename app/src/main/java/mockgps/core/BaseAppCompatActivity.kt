package mockgps.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

open class BaseAppCompatActivity: AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  private fun getAppViewModelFactory() = (applicationContext as App).appViewModelFactory

  fun <T : ViewModel> getViewModel(klass: Class<T>) = ViewModelProviders.of(this, getAppViewModelFactory()).get(klass)
}