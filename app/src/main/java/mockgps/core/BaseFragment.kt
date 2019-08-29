package mockgps.core

import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

open class BaseFragment : Fragment() {

  fun getAppViewModelFactory() = (context?.applicationContext as App).appViewModelFactory

  fun <T : ViewModel> getViewModel(klass: Class<T>) = ViewModelProviders.of(this, getAppViewModelFactory()).get(klass)

  fun dismissSoftInput(context: Context, windowToken: IBinder) {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
      hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
  }
}