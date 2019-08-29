package mockgps.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.saketh.mockgps.R

class SignupLoginFragment : androidx.fragment.app.Fragment() {

  companion object {
    fun newInstance() = SignupLoginFragment()
  }

  private lateinit var viewModel: SignupLoginViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.signup_login_fragment, container, false)?.apply {
      findViewById<Button>(R.id.buttonLogin).setOnClickListener {
        //NavHostFragment.findNavController(this@SignupLoginFragment)
        findNavController()
            .navigate(R.id.action_signupLoginFragment_to_mainFragment)
      }
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(SignupLoginViewModel::class.java)
    // TODO: Use the ViewModel
  }
}
