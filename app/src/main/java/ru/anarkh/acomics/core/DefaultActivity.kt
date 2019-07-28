package ru.anarkh.acomics.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.arkharov.statemachine.SavedStateRegistry

abstract class DefaultActivity: AppCompatActivity() {

	val stateRegistry = SavedStateRegistry(javaClass.simpleName)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		stateRegistry.restoreState(savedInstanceState)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		stateRegistry.saveState(outState)
	}
}