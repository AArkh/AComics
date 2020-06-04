package ru.anarkh.acomics.core

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import ru.anarkh.acomics.core.coroutines.CoroutineScope
import ru.arkharov.statemachine.SavedStateRegistry

abstract class DefaultFragment: Fragment() {

	protected val stateRegistry = SavedStateRegistry(javaClass.simpleName)
	protected lateinit var coroutineScope: CoroutineScope

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		coroutineScope = ViewModelProvider(this).get(CoroutineScope::class.java)
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

	fun getViewLifecycle(): Lifecycle {
		return viewLifecycleOwner.lifecycle
	}
}