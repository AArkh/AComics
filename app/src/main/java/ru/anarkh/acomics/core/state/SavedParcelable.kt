package ru.anarkh.acomics.core.state

import android.os.Bundle
import android.os.Parcelable

class SavedParcelable<T : Parcelable>(
	val key: String,
	var value: T? = null
) : Savable {

	override fun saveState(outState: Bundle) {
		outState.putParcelable(key, value)
	}

	override fun restoreState(savedState: Bundle?) {
		value = savedState?.getParcelable(key) as T? ?: value
	}
}