package com.saketh.mockgps.actions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.saketh.mockgps.R
import com.saketh.mockgps.arch.Event
import mockgps.actions.DevSettingsPresenter
import mockgps.actions.SearchPresenter
import mockgps.actions.UiOrchestrator
import mockgps.core.BaseFragment
import mockgps.model.Location
import mockgps.model.ParcelLocation
import mockgps.model.SavedPlace
import mockgps.simulator.EXTRA_PARCEL_LOC
import mockgps.simulator.INTENT_ACTION_MOCK_LOCATION
import mockgps.simulator.MockLocationService
import utils.toLocation
import java.util.*

class ActionsFragment : BaseFragment() {

  private lateinit var actionsLayout: View
  private lateinit var searchView: TextView
  private lateinit var mockView: View
  private lateinit var devSettingsView: View

  private lateinit var searchPresenter: SearchPresenter
  private lateinit var mockPresenter: MockPresenter
  private lateinit var devSettingsPresenter: DevSettingsPresenter

  private lateinit var orchestrator: UiOrchestrator

  private lateinit var mockAnimation: AnimationDrawable

  private lateinit var viewModel: ActionsViewModel
  private lateinit var handler: Handler

  private lateinit var receiver: BroadcastReceiver

  companion object {
    fun newInstance() = ActionsFragment()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    viewModel = getViewModel(ActionsViewModel::class.java)
    val view = inflater.inflate(R.layout.fragment_actions, container, false).apply {
      actionsLayout = findViewById(R.id.actionsLayout)
      BottomSheetBehavior.from(actionsLayout).state = BottomSheetBehavior.STATE_EXPANDED

      searchView = findViewById(R.id.textSearch)
      mockView = findViewById(R.id.mockContainer)
      devSettingsView = findViewById<View>(R.id.dev_settings_container)
    }

    searchPresenter = SearchPresenter(searchView, this)
    mockPresenter = MockPresenter(mockView, viewModel, this)
    devSettingsPresenter = DevSettingsPresenter(devSettingsView, viewModel.mockable, this)

    Log.d("devsak", "ActionsFragment: onCreateView: ")

    val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
    viewModel.windowInsetTop.observe(this, Observer<Int> { it -> it?.let { layoutParams.topMargin += it } })
    viewModel.windowInsetBottom.observe(this, Observer<Int> { it -> it?.let { layoutParams.bottomMargin += it } })
    view.layoutParams = layoutParams

    // TODO whose concern is this? ////////////////////
    receiver = object: BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { intent ->
          if (INTENT_ACTION_MOCK_LOCATION.equals(intent.action)) {
            val loc = intent.getParcelableExtra<ParcelLocation>(EXTRA_PARCEL_LOC)
            loc?.let {
              it.toLocation().apply {
                viewModel.locationEvent.value = Event<Location>(this)
                viewModel.onMockerStarted(this)
              }
            }
          }
        }
      }
    }
    LocalBroadcastManager.getInstance(requireContext())
        .registerReceiver(receiver, IntentFilter(INTENT_ACTION_MOCK_LOCATION))
    // TODO whose concern is this? ////////////////////

    return view
  }

  override fun onDestroyView() {
    super.onDestroyView()
    LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    Log.d("devsak", "ActionsFragment:  onActivityCreated")
    handler = Handler()
    orchestrator = UiOrchestrator(searchPresenter, actionsLayout, handler, viewModel)
    searchPresenter.load()
    mockPresenter.load(orchestrator.getMockVisibilityListener())
    devSettingsPresenter.load()
    viewModel.load()
  }

  override fun onResume() {
    super.onResume()
    Log.d("devsak", "ActionsFragment:  onResume")
    orchestrator.postPadding()
    context?.let { MockLocationService.checkMock(it) }
  }

  override fun onPause() {
    super.onPause()
    handler.removeCallbacksAndMessages(null)
  }

  private fun setPadding() {
    var location = IntArray(2)
    actionsLayout.getLocationInWindow(location)
    viewModel.paddingBottom.value = Event(location[1])
  }
}

class SavedPlacesAdapter(val savedPlaces: ArrayList<SavedPlace>, val viewModel: ActionsViewModel) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SavedPlacesAdapter.ViewHolder>() {
  class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_saved_place, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val savedPlace = savedPlaces[position]
    holder.view.apply {
      findViewById<TextView>(R.id.textViewLocName).text = savedPlace.name
      findViewById<TextView>(R.id.viewPlaceLatLng).text = "${savedPlace.latitude}, ${savedPlace.longitude}"
      setOnClickListener {
        Toast.makeText(context, "TODO: Use ${savedPlace.name} for mock", Toast.LENGTH_SHORT).show() // TODO
      }
    }
  }

  override fun getItemCount(): Int {
    return when {
      savedPlaces.size >= 3 -> 3
      else -> savedPlaces.size
    }
  }
}