package mockgps.actions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.saketh.mockgps.R
import com.saketh.mockgps.actions.ActionsViewModel
import mockgps.model.Location
import utils.copy

class SavePlaceDialog(
    location: Location,
    viewModel: ActionsViewModel,
    context: Context) : BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {

  init {
    val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_save, null).apply {
      findViewById<TextView>(R.id.textViewLocName).text = location.primaryName
      findViewById<TextView>(R.id.textViewLatLng).text = location.getDisplayLatLng()
      val saveText = findViewById<TextView>(R.id.textViewSave)
      val editText = findViewById<TextInputEditText>(R.id.editTextAlias).apply {
        setText(location.alias)
        addTextChangedListener(object : TextWatcher {
          override fun afterTextChanged(s: Editable?) {}
          override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (count == 0) saveText.text = "Skip & Save"
            else saveText.text = "Save"
          }

          override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (count == 0) saveText.text = "Skip & Save"
            else saveText.text = "Save"
          }
        })
      }
      saveText.setOnClickListener {
        viewModel.save(location.copy(editText.text.toString()))
        dismiss()
      }
      findViewById<TextView>(R.id.textViewCancel).setOnClickListener { dismiss() }
      setOnShowListener {
        val bottomSheet =
            this@SavePlaceDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        showSoftKeyboard(editText)
      }
    }

    setContentView(sheetView)
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
  }

  private fun showSoftKeyboard(view: View) {
    if (view.requestFocus()) {
      val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
  }
}