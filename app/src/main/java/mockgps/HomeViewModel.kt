package mockgps

import androidx.lifecycle.MutableLiveData
import mockgps.core.Provider
import mockgps.core.ScopedViewModel
import mockgps.core.scope.Scope


class HomeViewModel(
    mapScope: Scope
) : ScopedViewModel(mapScope) {

  lateinit var windowInsetTop: MutableLiveData<Int>
  lateinit var windowInsetBottom: MutableLiveData<Int>

  override fun onInject(provider: Provider) {
    (provider as HomeProvider).let {
      windowInsetTop = it.windowInsetTop
      windowInsetBottom = it.windowInsetBottom
    }
  }

  override fun onDestroy() {
  }
}