package ru.anarkh.acomics.core.state

import android.os.Bundle

interface Savable {
	fun saveState(outState: Bundle)
	fun restoreState(savedState: Bundle?)
}