package mockgps


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.saketh.mockgps.R

class RouterFragment : androidx.fragment.app.Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    Log.d("devsak", "RouterFragment : onCreateView")
    return null
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    Log.d("devsak", "RouterFragment : onActivityCreated")
    findNavController().navigate(R.id.action_routerFragment_to_signupLoginFragment)
  }

  override fun onStart() {
    super.onStart()
    Log.d("devsak", "RouterFragment : onStart")
  }

  override fun onResume() {
    super.onResume()
    Log.d("devsak", "RouterFragment : onResume")
  }


}
