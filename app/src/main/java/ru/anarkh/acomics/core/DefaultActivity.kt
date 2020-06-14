package ru.anarkh.acomics.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.anarkh.acomics.core.coroutines.ObservableScope
import ru.arkharov.statemachine.SavedStateRegistry

abstract class DefaultActivity: AppCompatActivity() {

	lateinit var coroutineScope: ObservableScope

	protected val stateRegistry = SavedStateRegistry(javaClass.simpleName)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		coroutineScope = ViewModelProvider(this).get(ObservableScope::class.java)
		stateRegistry.restoreState(savedInstanceState)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		stateRegistry.saveState(outState)
	}

	override fun onPause() {
		super.onPause()
		if (isFinishing) {
			coroutineScope.cancelScope()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		if (isChangingConfigurations) {
			coroutineScope.removeObservers()
		}
	}

	override fun onBackPressed() {
		val fragment = supportFragmentManager.primaryNavigationFragment as? DefaultFragment
		if (fragment != null && fragment.onBack()) {
			return
		}
		super.onBackPressed()
	}
}