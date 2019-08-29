package mockgps.actions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import mockgps.core.BaseFragment

const val ANIM_DURATION = 200L

class SearchPresenter (
    private val searchView: View,
    private val frag: BaseFragment
) {

  init {
    searchView.setOnClickListener {
      // TODO move navigation concerns to fragment
      val extras = FragmentNavigatorExtras(searchView to "search_search")
      frag.findNavController().navigate(com.saketh.mockgps.R.id.action_actionsFragment_to_searchFragment,
          null, null, /*extras*/ null)
    }
  }

  fun load() {}

  fun show() {
    searchView.visibility = View.VISIBLE
    if (searchView.translationY == 0f) {
      return
    }
    searchView.animate()
        //.yBy(-100f)
        .translationY(0f)
        .alpha(1f)
        .setDuration(ANIM_DURATION)
        .setInterpolator(AccelerateInterpolator())
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            searchView.visibility = View.VISIBLE
          }
        })
  }

  fun hide() {
    searchView.animate()
        //.yBy(100f)
        .translationY(searchView.height.toFloat())
        .alpha(0f)
        .setDuration(ANIM_DURATION)
        .setInterpolator(DecelerateInterpolator())
        .setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        searchView.visibility = View.GONE
      }
    })
  }

}