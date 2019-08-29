package mockgps.core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mockgps.HomeProvider
import mockgps.HomeViewModel
import mockgps.actions.ActionsApiService
import mockgps.actions.ActionsProvider
import com.saketh.mockgps.actions.ActionsViewModel
import mockgps.core.scope.Scope
import mockgps.map.MapProvider
import mockgps.map.MapViewModel
import mockgps.search.SearchApiService
import mockgps.search.SearchProvider
import mockgps.search.SearchViewModel

class AppViewModelFactory(val appScope: Scope) : ViewModelProvider.Factory {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return when {
      modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
        createHomeViewModel() as T
      }

      modelClass.isAssignableFrom(MapViewModel::class.java) -> {
        createMapViewModel() as T
      }
      modelClass.isAssignableFrom(ActionsViewModel::class.java) -> {
        createActionsViewModel() as T
      }
      modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
        createSearchViewModel()as T
      }
      else -> null as T // FIXME
    }
  }

  private fun createHomeViewModel() : HomeViewModel {
    val homeScope = Scope("home", provider = HomeProvider())
    appScope.add(homeScope)
    return HomeViewModel(homeScope)
  }

  private fun createMapViewModel() : MapViewModel {
    val homeScope = appScope.getChild("home")
    homeScope?.let {
      val homeProvider = homeScope.provider as HomeProvider

      val mapScope = Scope("map",
          MapProvider(homeProvider.windowInsetTop, homeProvider.windowInsetBottom))
      homeScope.add(mapScope)
      return MapViewModel(mapScope)
    }

    throw IllegalStateException("Scope(\"home\") not inited.")
  }

  private fun createActionsViewModel() : ActionsViewModel {
    Log.d("devsak", "appScope: " + appScope.treeout())
    val homeScope = appScope.getChild("home")
    val mapScope = homeScope?.getChild("map")

    if (homeScope != null && mapScope != null) {
      val homeProvider = homeScope.provider as HomeProvider
      val mapProvider = mapScope.provider as MapProvider
      val appProvider = appScope.provider as AppProvider
      val actionsScope = Scope("actions", ActionsProvider(appProvider.repository,
          ActionsApiService(appProvider.geoDataClient), appProvider.mockLocationDelegate,
          mapProvider.paddingBottom, mapProvider.selectedLocation, mapProvider.mapTappedLatLng,
          mapProvider.mockLocation, homeProvider.windowInsetTop, homeProvider.windowInsetBottom,
          appProvider.config.mockable))
      mapScope.add(actionsScope)
      return ActionsViewModel(actionsScope)
    }

    throw IllegalStateException("Scope(\"map\") not inited.")
  }

  private fun createSearchViewModel() : SearchViewModel {
    val homeScope = appScope.getChild("home")
    val mapScope = homeScope?.getChild("map")

    if (homeScope != null && mapScope != null) {
      val homeProvider = homeScope.provider as HomeProvider
      val mapProvider = mapScope.provider as MapProvider
      val appProvider = appScope.provider as AppProvider
      val searchScope = Scope("search", SearchProvider(appProvider.repository,
          SearchApiService(appProvider.geoDataClient), mapProvider.selectedLocation,
          homeProvider.windowInsetTop, homeProvider.windowInsetBottom))
      mapScope.add(searchScope)
      return SearchViewModel(searchScope)
    }

    throw IllegalStateException("Scope(\"map\") not inited.")
  }

    // AppScope
    // HomeScope
    // MapScope         mockLocationEvent                     paddingBottom
    //  #PreMockScope
    //   SearchScope                        selectedLocation
    //   ActionsScope   mockLocationEvent   selectedLocation  paddingBottom
    //  #MockScope
    //   ActionsScope

    // create MapScope and its Provider

}