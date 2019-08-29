package mockgps

import androidx.lifecycle.MutableLiveData
import mockgps.core.Provider

class HomeProvider : Provider() {

  val windowInsetTop = MutableLiveData<Int>()
  val windowInsetBottom = MutableLiveData<Int>()
}