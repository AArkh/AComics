package ru.arkharov.statemachine

import android.os.Bundle

interface Savable {
	fun saveState(outState: Bundle)
	fun restoreState(savedState: Bundle?)
}