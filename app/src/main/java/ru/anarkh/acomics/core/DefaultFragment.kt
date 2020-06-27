package ru.anarkh.acomics.core

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import ru.anarkh.acomics.core.coroutines.ObservableScope
import ru.anarkh.acomics.core.state.SavedStateRegistry

abstract class DefaultFragment: Fragment() {

	val activityResultObservers = SparseArray<(resultCode: Int, data: Intent?) -> Boolean>()

	protected val stateRegistry = SavedStateRegistry(javaClass.simpleName)
	protected val backButtonController = BackButtonController()
	protected lateinit var coroutineScope: ObservableScope

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		coroutineScope = ViewModelProvider(this).get(ObservableScope::class.java)
		stateRegistry.restoreState(savedInstanceState)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		stateRegistry.saveState(outState)
	}

	override fun onDestroy() {
		super.onDestroy()
		if (requireActivity().isChangingConfigurations) {
			coroutineScope.removeObservers()
		} else {
			coroutineScope.cancelScope()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		val observer = activityResultObservers[requestCode]
		if (observer?.invoke(resultCode, data) == true) {
			return
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	fun getViewLifecycle(): Lifecycle {
		return viewLifecycleOwner.lifecycle
	}

	fun getParentScope(): ObservableScope {
		return (requireActivity() as DefaultActivity).coroutineScope
	}

	/**
	 * @return true, если обработано нажатие на back.
	 */
	fun onBack(): Boolean = backButtonController.onBack()
}

class BackButtonController {

	var onBackListener: (() -> Boolean)? = null

	fun onBack(): Boolean {
		return onBackListener?.invoke() ?: false
	}
}