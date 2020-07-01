package ru.anarkh.acomics.main.catalog.widget

import android.widget.EditText
import androidx.annotation.IdRes
import androidx.constraintlayout.motion.widget.MotionLayout
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.view.DelayedInputTextWatcher
import ru.anarkh.acomics.main.catalog.widget.paging.PagingState
import ru.anarkh.acomics.main.catalog.widget.paging.SearchContent

class CatalogSearchWidget(
	private val editText: EditText,
	private val motionLayout: MotionLayout
) {

	var searchInputCallback: ((text: String) -> Unit)? = null
	var openSearchCallback: (() -> Unit)? = null
	var closeSearchCallback: (() -> Unit)? = null

	init {
		val textWatcher = DelayedInputTextWatcher({ userInput: String ->
			searchInputCallback?.invoke(userInput)
		})
		motionLayout.setTransitionListener(TransitionCompletedListener { state: Int ->
			if (state == R.id.start) {
				editText.removeTextChangedListener(textWatcher)
				editText.setText("")
				editText.clearFocus()
				closeSearchCallback?.invoke()
			} else {
				editText.addTextChangedListener(textWatcher)
				editText.requestFocus()
				openSearchCallback?.invoke()
			}
		})
	}

	fun updateState(state: PagingState) {
		if (state !is SearchContent) {
			return
		}
		if (motionLayout.progress == 0f) {
			motionLayout.progress = 1f
		}
		if (editText.text.toString() != state.searchInput) {
			editText.setText(state.searchInput)
		}
	}
}

private class TransitionCompletedListener(
	private val listener: (state: Int) -> Unit
) : MotionLayout.TransitionListener {

	override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
	override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
	override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
	override fun onTransitionCompleted(p0: MotionLayout?, @IdRes state: Int) {
		listener.invoke(state)
	}
}