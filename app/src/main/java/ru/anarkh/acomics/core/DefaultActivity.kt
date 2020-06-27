package ru.anarkh.acomics.core

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.anarkh.acomics.core.coroutines.ObservableScope
import ru.anarkh.acomics.core.state.SavedStateRegistry

abstract class DefaultActivity: AppCompatActivity() {

	val activityResultObservers = SparseArray<(resultCode: Int, data: Intent?) -> Boolean>()

	lateinit var coroutineScope: ObservableScope

	protected val backButtonController = BackButtonController()
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
		if (backButtonController.onBack()) {
			return
		}
		super.onBackPressed()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		val observer = activityResultObservers[requestCode]
		if (observer?.invoke(resultCode, data) == true) {
			return
		}
		super.onActivityResult(requestCode, resultCode, data)
	}
}