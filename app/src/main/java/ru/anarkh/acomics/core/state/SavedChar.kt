package ru.anarkh.acomics.core.state

import android.os.Bundle

class SavedChar(
	val key: String,
	var value: Char
) : Savable {

	override fun saveState(outState: Bundle) {
		outState.putChar(key, value)
	}

	override fun restoreState(savedState: Bundle?) {
		value = savedState?.getChar(key) ?: value
	}
}