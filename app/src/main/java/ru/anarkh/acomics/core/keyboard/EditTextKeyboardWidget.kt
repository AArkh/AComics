package ru.anarkh.acomics.core.keyboard

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


class EditTextKeyboardWidget(
	private val editText: EditText
) {

	private val inputManager: InputMethodManager = editText.context
		.getSystemService(Context.INPUT_METHOD_SERVICE)
		as InputMethodManager

	fun show() {
		inputManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
	}

	fun hide() {
		inputManager.hideSoftInputFromWindow(editText.windowToken, 0)
	}
}