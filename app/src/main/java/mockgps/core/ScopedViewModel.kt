package mockgps.core

import androidx.lifecycle.ViewModel
import mockgps.core.scope.Scope


abstract class ScopedViewModel(val scope: Scope): ViewModel() {

  init {
    scope.provider?.let {
      onInject(it)
    }
  }

  abstract fun onInject(provider: Provider)

  /**
   * Do not use. Should not be overridden. Implement onDestroy for clearing up objects.
   */
  override fun onCleared() {
    onDestroy()
    detachScope()
  }

  abstract fun onDestroy()

  private fun detachScope() {
    scope.finish()
  }

}