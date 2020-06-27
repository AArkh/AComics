package ru.anarkh.acomics.core.state

import android.os.Bundle

class SavedBoolean(
	val key: String,
	var value: Boolean = false
) : Savable {

	override fun saveState(outState: Bundle) {
		outState.putBoolean(key, value)
	}

	override fun restoreState(savedState: Bundle?) {
		value = savedState?.getBoolean(key) ?: value
	}
}