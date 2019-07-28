package ru.arkharov.statemachine

import android.os.Bundle

class SavedString(
    val key: String,
    var value: String? = null
) : Savable {

    override fun saveState(outState: Bundle) {
        outState.putString(key, value)
    }

    override fun restoreState(savedState: Bundle?) {
        value = savedState?.getString(key) ?: value
    }
}