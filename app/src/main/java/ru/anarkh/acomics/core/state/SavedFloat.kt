package ru.arkharov.statemachine

import android.os.Bundle

class SavedFloat(
    val key: String,
    var value: Float = 0f
) : Savable {

    override fun saveState(outState: Bundle) {
        outState.putFloat(key, value)
    }

    override fun restoreState(savedState: Bundle?) {
        value = savedState?.getFloat(key) ?: value
    }
}