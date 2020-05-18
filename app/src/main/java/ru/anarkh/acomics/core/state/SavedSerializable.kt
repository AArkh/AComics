package ru.arkharov.statemachine

import android.os.Bundle
import java.io.Serializable

class SavedSerializable<T : Serializable> (
    private val key: String,
    var value: T? = null
) : Savable {

    override fun saveState(outState: Bundle) {
        outState.putSerializable(key, value)
    }

    override fun restoreState(savedState: Bundle?) {
        value = savedState?.getSerializable(key) as T? ?: value
    }
}