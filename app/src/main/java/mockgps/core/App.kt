package mockgps.core

import android.app.Application
import mockgps.core.scope.Scope

class App : Application() {

  lateinit var appViewModelFactory: AppViewModelFactory
  lateinit var appScope: Scope

  override fun onCreate() {
    super.onCreate()

    appScope = Scope("app_scope", provider = AppProvider(applicationContext))
    appViewModelFactory = AppViewModelFactory(appScope)
  }

  fun getConfig() = (appScope.provider as AppProvider).config

  fun getMocker() = (appScope.provider as AppProvider).mocker

}