package ru.anarkh.acomics.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.anarkh.acomics.core.coroutines.ActivityScope
import ru.arkharov.statemachine.SavedStateRegistry

abstract class DefaultActivity: AppCompatActivity() {

	protected val stateRegistry = SavedStateRegistry(javaClass.simpleName)
	protected lateinit var activityScope: ActivityScope

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activityScope = ViewModelProvider(this).get(ActivityScope::class.java)
		stateRegistry.restoreState(savedInstanceState)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		stateRegistry.saveState(outState)
	}

	override fun onPause() {
		super.onPause()
		if (isFinishing) {
			activityScope.cancelScope()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		if (isChangingConfigurations) {
			activityScope.removeObservers()
		}
	}
}