package ru.arkharov.statemachine

import android.os.Bundle

class SavedStateRegistry(private val key: String) : StateRegistry, Savable {

	private val properties = HashSet<Savable>()

	private var bundle: Bundle? = null

	override fun register(savable: Savable) {
		savable.restoreState(bundle)
		properties.remove(savable)
		properties.add(savable)
	}

	override fun unregister(savable: Savable) {
		properties.remove(savable)
	}

	override fun saveState(outState: Bundle) {
		val savedProperies = Bundle()
		properties.forEach { it.saveState(savedProperies) }
		outState.putBundle(key, savedProperies)
	}

	override fun restoreState(savedState: Bundle?) {
		bundle = savedState?.getBundle(key)
		properties.forEach { it.restoreState(bundle) }
	}
}