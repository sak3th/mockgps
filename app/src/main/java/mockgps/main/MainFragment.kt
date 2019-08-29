package mockgps.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.saketh.mockgps.R
import mockgps.core.BaseFragment
import kotlinx.android.synthetic.main.action_search.view.*

class MainFragment : BaseFragment(), CardListener {

  override fun onCardClicked() {

  }

  companion object {
    fun newInstance() = MainFragment()
  }

  private lateinit var mapLayout: ConstraintLayout
  //private lateinit var bottomLayout: RecyclerView
  private lateinit var bottomLayout: ConstraintLayout

  private lateinit var recyclerCards: androidx.recyclerview.widget.RecyclerView
  private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
  private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.content_main, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
  }
}

class CardsAdapter(
    private val cards: Array<String>,
    private val listener: CardListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<CardsAdapter.ViewHolder>() {

  class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

  override fun onCreateViewHolder(parent: ViewGroup,
                                  viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.action_search, parent, false)
    val textView = view.findViewById<TextView>(R.id.textSearch).apply {
      setOnClickListener {
        listener.onCardClicked()
      }
    }
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.view.findViewById<EditText>(R.id.textSearch)?.apply {
      textSearch.setText(cards[position])
    }
  }

  override fun getItemCount() = cards.size
}

interface CardListener {
  fun onCardClicked()
}
