package mockgps.actions

import android.os.Handler
import android.view.View
import com.saketh.mockgps.actions.ActionsViewModel
import com.saketh.mockgps.arch.Event
import ui.OnVisibilityChangeListener

class UiOrchestrator(
    private val searchPresenter: SearchPresenter,
    private val actionsLayout: View,
    private val handler: Handler,
    private val viewModel: ActionsViewModel) {

  fun getMockVisibilityListener() = object : OnVisibilityChangeListener() {
    override fun onVisible() {
      searchPresenter.show()
      postPadding()
    }

    override fun onGone() {
      searchPresenter.hide()
      postPadding()
    }
  }

  fun postPadding() {
    handler.postDelayed({ setPadding() }, 200)
  }

  private fun setPadding() {
    var location = IntArray(2)
    actionsLayout.getLocationInWindow(location)
    viewModel.paddingBottom.value = Event(location[1])
  }
}
// TODO viewmodel setPadding feels like code smell