package com.saketh.mockgps.actions

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.saketh.mockgps.R
import mockgps.core.BaseFragment
import mockgps.model.Location
import kotlinx.android.synthetic.main.action_mock_c.view.*
import mockgps.actions.SavePlaceDialog
import ui.OnVisibilityChangeListener

class MockPresenter (
    private val mockView: View,
    private val viewModel: ActionsViewModel,
    private val frag: BaseFragment
) {

  private var visibilityChangeListener: OnVisibilityChangeListener? = null
  private val mockable: LiveData<Boolean>

  init {
    mockView.visibility = View.GONE
    frag.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onDestroy(owner: LifecycleOwner) {
        onDestroy()
      }
    })
    mockable = viewModel.mockable
  }

  fun load(listener: OnVisibilityChangeListener) {
    visibilityChangeListener = listener
    observeViewModel(viewModel, frag)
    resetMockUi(mockView)
    mockable.apply {
      removeObservers { frag.lifecycle }
      mockView.textViewStartMock.isEnabled = value != null && value == true
    }
  }

  private fun observeViewModel(viewModel: ActionsViewModel, lifecycleOwner: LifecycleOwner) {
    viewModel.locationEvent.removeObservers(lifecycleOwner)
    viewModel.locationEvent.observe(lifecycleOwner, Observer {
      it?.peekContent()?.let { loc ->
        Log.d("devsak", "locationEvent: observe: $loc")
        onMockLocationSet(loc, mockView)
      }
    })

    viewModel.mockLocation().observe(lifecycleOwner, Observer {
      if (it == null) {
        resetMockUi(mockView)
      } else {
        setMockUi(mockView)
      }
      // TODO replace with below
      /*location?.let { resetMockUi(mockView) }
          ?: setMockUi(mockView)*/
    })
  }

  private fun onMockLocationSet(location: Location, mockView: View) {
    Log.d("devsak", "onMockLocationSet: $location")
    mockView.textViewLocName.text = location.getAliasOrName()

    if (location.hasLatLng()) {
      mockView.textViewLatLng.apply {
        visibility = View.VISIBLE
        text = location.getDisplayLatLng()
      }
      mockView.shimmerLatLng.apply {
        visibility = View.GONE
        stopShimmer()
      }
    } else {
      mockView.textViewLatLng.visibility = View.INVISIBLE
      mockView.shimmerLatLng.apply {
        visibility = View.VISIBLE
        startShimmer()
      }
    }

    mockView.textViewStartMock.setOnClickListener {
      viewModel.startMock(location)
    }
    mockView.imageViewSave.apply {
      visibility = View.VISIBLE
      isSelected = (location.alias != null)

      setOnClickListener {
        if (location.alias == null) {
          showSaveDailog(frag, location, viewModel)
        } else {
          viewModel.unsave(location)
        }
      }
    }
    mockView.visibility = View.VISIBLE
  }

  private fun resetMockUi(mockView: View) {
    /*mockView.mockActionsGroup.visibility = View.GONE
    mockView.textViewStartMock.visibility = View.VISIBLE*/

    val fadeOut = AnimationUtils.loadAnimation(mockView.context, R.anim.abc_fade_out)
    fadeOut.duration = 200
    fadeOut.fillAfter = false
    fadeOut.setAnimationListener(object: Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {}
      override fun onAnimationStart(animation: Animation?) {}
      override fun onAnimationEnd(animation: Animation?) {
        mockView.mockActionsGroup.visibility = View.GONE
      }
    })
    mockView.textViewStopMock.startAnimation(fadeOut)
    mockView.textViewAccTitle.startAnimation(fadeOut)
    mockView.textViewAcc.startAnimation(fadeOut)

    val fadeIn = AnimationUtils.loadAnimation(mockView.context, R.anim.abc_fade_in)
    fadeIn.duration = 200
    fadeIn.startOffset = 200
    fadeIn.fillBefore = true
    fadeIn.fillAfter = true
    fadeIn.setAnimationListener(object: Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {}
      override fun onAnimationStart(animation: Animation?) {
        mockView.textViewStartMock.isEnabled = mockable.value != null && mockable.value == true
        mockView.textViewStartMock.visibility = View.VISIBLE

      }
      override fun onAnimationEnd(animation: Animation?) {
        mockView.textViewStartMock.isEnabled = mockable.value != null && mockable.value == true
        mockView.textViewStartMock.visibility = View.VISIBLE
      }
    })
    mockView.textViewStartMock.startAnimation(fadeIn)

    visibilityChangeListener?.onVisible()
  }

  private fun setMockUi(mockView: View) {
    val fadeOut = AnimationUtils.loadAnimation(mockView.context, R.anim.abc_fade_out)
    fadeOut.duration = 200
    fadeOut.fillAfter = false
    fadeOut.setAnimationListener(object: Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {}
      override fun onAnimationStart(animation: Animation?) {}
      override fun onAnimationEnd(animation: Animation?) {
        mockView.textViewStartMock.isEnabled = mockable.value != null && mockable.value == true
        mockView.textViewStartMock.visibility = View.GONE
      }
    })
    mockView.textViewStartMock.startAnimation(fadeOut)

    val fadeIn = AnimationUtils.loadAnimation(mockView.context, R.anim.abc_fade_in)
    fadeIn.duration = 200
    fadeIn.startOffset = 200
    fadeIn.fillBefore = true
    fadeIn.fillAfter = true
    fadeIn.setAnimationListener(object: Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {}
      override fun onAnimationStart(animation: Animation?) {
        mockView.mockActionsGroup.visibility = View.VISIBLE
      }
      override fun onAnimationEnd(animation: Animation?) {
        mockView.mockActionsGroup.visibility = View.VISIBLE
      }
    })
    mockView.textViewStopMock.startAnimation(fadeIn)
    mockView.textViewAccTitle.startAnimation(fadeIn)
    mockView.textViewAcc.startAnimation(fadeIn)

    /*mockView.mockActionsGroup.visibility = View.VISIBLE
    mockView.textViewStartMock.visibility = View.GONE*/

    mockView.textViewStopMock.setOnClickListener {
      viewModel.stopMock()
    }

    visibilityChangeListener?.onGone()
  }

  private var saveDialog: SavePlaceDialog? = null
  fun showSaveDailog(frag: Fragment, location: Location, viewModel: ActionsViewModel) {
    saveDialog?.dismiss()
    saveDialog = null
    frag.context?.let { cxt ->
      saveDialog = SavePlaceDialog(location, viewModel, cxt).apply {
        show()
      }
    }
  }

  fun onDestroy () {
    saveDialog?.dismiss()
    saveDialog = null
  }
}