package mockgps.search

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.saketh.mockgps.R
import mockgps.core.BaseFragment
import mockgps.model.Location
import kotlinx.android.synthetic.main.search_fragment.*

class SearchFragment : BaseFragment() {

  companion object {
    fun newInstance() = SearchFragment()
  }

  private lateinit var viewModel: SearchViewModel

  private lateinit var rootView: View
  private lateinit var drawer: androidx.drawerlayout.widget.DrawerLayout
  private lateinit var searchLayout: ViewGroup
  private lateinit var search: EditText
  private lateinit var cancel: ImageView
  private lateinit var progress: ProgressBar
  private lateinit var searchShimmer: ShimmerFrameLayout
  private lateinit var searchResultsHeader: TextView
  private lateinit var recyclerResults: androidx.recyclerview.widget.RecyclerView
  private lateinit var recyclerSavedLocations: androidx.recyclerview.widget.RecyclerView

  private var snackbar: Snackbar? = null

  private val searchResults = ArrayList<Location>()
  private val savedLocations = ArrayList<Location>()

  @SuppressLint("NewApi")
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    viewModel = getViewModel(SearchViewModel::class.java)
    Log.d("devsak", "onCreateView: ")
    rootView = inflater.inflate(R.layout.search_fragment, container, false).apply {

      drawer = findViewById(R.id.searchDrawer)
      val lpDrawer = drawer.layoutParams as ViewGroup.MarginLayoutParams
      viewModel.windowInsetTop.value?.let {
        //lpDrawer.topMargin += it
      }
      viewModel.windowInsetBottom.value?.let {
        lpDrawer.bottomMargin += it
      }
      drawer.layoutParams = lpDrawer

      searchLayout = findViewById(R.id.searchLayout)
      val lpSearch = searchLayout.layoutParams as ViewGroup.MarginLayoutParams
      viewModel.windowInsetTop.value?.let {
        lpSearch.topMargin += it
      }
      searchLayout.layoutParams = lpSearch

      findViewById<View>(R.id.imageViewBack).apply {
        setOnClickListener {
          activity?.let {
            val inputMgr = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMgr.isActive) {
              if (it.currentFocus != null) {
                inputMgr.hideSoftInputFromWindow(it.currentFocus.windowToken, 0)
              }
            }
          }
          findNavController().navigateUp()
        }
      }

      cancel = findViewById<ImageView>(R.id.imageViewCancel)
      cancel.visibility = View.GONE

      progress = findViewById<ProgressBar>(R.id.progressBarSearch)
      progress.visibility = View.GONE

      searchShimmer = findViewById<ShimmerFrameLayout>(R.id.shimmerSearch)

      search = findViewById<EditText>(R.id.textSearch).apply {
        setOnEditorActionListener { editText, actionId, event ->
          Log.d("devsak", "${actionId} : ${event?.action} : ${editText.text}")
          when (actionId) {
            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO, EditorInfo.IME_ACTION_SEARCH -> {
              dismissSoftInput(context, windowToken)
              onSearchClicked(editText.text)
            }
          }
          true
        }
        addTextChangedListener(object: TextWatcher {
          override fun afterTextChanged(s: Editable?) {}
          override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
          override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (count == 0) cancel.visibility = View.GONE
            else cancel.visibility = View.VISIBLE
          }
        })
      }

      cancel.setOnClickListener {
        search.text.clear()
        cancel.visibility = View.GONE
      }

      searchResultsHeader = findViewById<TextView>(R.id.textViewSearchResultsHeader)

      recyclerResults = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerSearchResults).apply {
        setHasFixedSize(true)
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        adapter = ResultsAdapter(searchResults, viewModel)
      }

      recyclerSavedLocations = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerSavedLocations).apply {
        setHasFixedSize(true)
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        adapter = SavedLocationsAdapter(savedLocations, viewModel)
      }
    }

    return rootView
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel.searchResults.observe(this, Observer {
      Log.d("devsak", "placeIds : ${it?.size}")
      onSearchCompleted("")
      it?.let {
        when (it.size) {
          0 -> Toast.makeText(context, "Sorry, no places found!", Toast.LENGTH_SHORT).show()
          else -> {
            searchResults.clear()
            searchResults.addAll(it)
            recyclerResults.adapter?.notifyDataSetChanged()
          }
        }
      }
    })
    viewModel.savedLocations.observe(this, Observer {
      it?.let {
        savedLocations.clear()
        savedLocations.addAll(it)
        recyclerSavedLocations.adapter?.notifyDataSetChanged()
      }
    })
    viewModel.navigateUp.observe(this, Observer {
      it?.let {
        it.getContentIfNotHandled()?.let {
          Log.d("devsak", "navigateUp")
          findNavController().navigateUp()
        }
      }
    })
    viewModel.searching.observe(this, Observer {
      when (it) {
        is SearchInProgress -> onSearchInProgress(it?.searchText)
        is SearchCancelled -> onSearchCancelled(it?.searchText)
        is SearchComplete -> onSearchCompleted(it?.searchText)
      }
    })

    viewModel.loadPreviousSearches()
    viewModel.loadTopSavedPlaces()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    snackbar?.dismiss()
  }

  private fun onSearchClicked(text: CharSequence) {
    viewModel.search(text.toString())
    animateToSearchResults()
  }

  private fun onSearchCompleted(searchText: String) {
    recyclerResults.visibility = View.VISIBLE
    progress.visibility = View.GONE
    shimmerSearch.stopShimmer()
    shimmerSearch.visibility = View.GONE
    cancel.visibility = View.VISIBLE
    snackbar?.dismiss()
  }

  private fun onSearchInProgress(searchText: String) {
    recyclerResults.visibility = View.GONE
    cancel.visibility = View.GONE
    progress.visibility = View.VISIBLE
    shimmerSearch.visibility = View.VISIBLE
    shimmerSearch.startShimmer()

    snackbar = Snackbar.make(rootView, "Searching for $searchText",
        Snackbar.LENGTH_INDEFINITE).apply {
      setAction("CANCEL") {
        viewModel.cancelSearch(searchText)
        onSearchCancelled(searchText)
      }

      val textView =  view.findViewById(R.id.snackbar_text) as TextView
      textView.setTextColor(Color.BLACK);
      textView.setSingleLine()
      setActionTextColor(Color.BLACK)

      val params = view.layoutParams as ViewGroup.MarginLayoutParams
      params.setMargins(12, 12, 12, 12);
      view.layoutParams = params

      view.background = context.getDrawable(R.drawable.bg_snackbar)
      ViewCompat.setElevation(view, 6f);

      show()
    }
  }

  private fun onSearchCancelled(searchText: String) {
    snackbar?.dismiss()
    recyclerResults.visibility = View.VISIBLE
    cancel.visibility = View.VISIBLE
    progress.visibility = View.GONE
    shimmerSearch.stopShimmer()
    shimmerSearch.visibility = View.GONE
  }

  private fun animateToSearchResults() {
    val fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.dock_left_enter)
    val fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.dock_left_exit)
    fadeOutAnim.reset()
    fadeInAnim.reset()
    fadeOutAnim.setAnimationListener(object : Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {}
      override fun onAnimationStart(animation: Animation?) {}
      override fun onAnimationEnd(animation: Animation?) {
        searchResultsHeader.text = "SEARCH RESULTS"

        searchResultsHeader.clearAnimation()
        searchResultsHeader.startAnimation(fadeInAnim)  }
    })
    searchResultsHeader.clearAnimation()
    searchResultsHeader.startAnimation(fadeOutAnim)

  }
}

class ResultsAdapter(
    val searchResults: List<Location>,
    val viewModel: SearchViewModel
): androidx.recyclerview.widget.RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {
  class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val searchResult = searchResults[position]
    holder.view.apply {
      findViewById<TextView>(R.id.textViewLocName).text = searchResult.primaryName
      findViewById<TextView>(R.id.viewPlaceLatLng).text = searchResult.secondaryName
      setOnClickListener {
        viewModel.onResultClick(searchResult)
        Toast.makeText(context, searchResult.placeId, Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun getItemCount(): Int {
    return searchResults.size
  }
}

class SavedLocationsAdapter(
    val locations: List<Location>,
    val viewModel: SearchViewModel
): androidx.recyclerview.widget.RecyclerView.Adapter<SavedLocationsAdapter.ViewHolder>() {
  class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_saved_location, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val location = locations[position]
    holder.view.apply {
      findViewById<TextView>(R.id.textViewLocName).text = location.getAliasOrName()
      findViewById<TextView>(R.id.viewPlaceLatLng).text = location.getDisplayLatLng()
      setOnClickListener {
        viewModel.onSavedLocationClick(location)
        Toast.makeText(context, location.placeId, Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun getItemCount(): Int {
    return locations.size
  }
}
