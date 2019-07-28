package ru.anarkh.acomics.core.state

import android.os.Bundle
import ru.arkharov.statemachine.Savable


class SavedInt(
	val key: String,
	var value: Int = 0
) : Savable {

	override fun saveState(outState: Bundle) {
		outState.putInt(key, value)
	}

	override fun restoreState(savedState: Bundle?) {
		value = savedState?.getInt(key) ?: value
	}
}