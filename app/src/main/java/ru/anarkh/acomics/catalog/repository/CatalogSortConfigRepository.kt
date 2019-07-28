package ru.anarkh.acomics.catalog.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import ru.anarkh.acomics.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.catalog.model.CatalogSortingBy

class CatalogSortConfigRepository(
	context: Context
) {

	companion object {
		const val EXTRA_SORT_CONFIG = "sort_config"
	}

	private val sharedPreferences = context.getSharedPreferences(EXTRA_SORT_CONFIG, MODE_PRIVATE)
	private val gson = Gson()
	private var sortConfig: CatalogSortConfig

	init {
		if (!sharedPreferences.contains(EXTRA_SORT_CONFIG)) {
			val defaultConfig = CatalogSortConfig(CatalogSortingBy.BY_DATE)
			sortConfig = defaultConfig
			updatePreferences(defaultConfig)
		} else {
			val stringRepresentation = sharedPreferences.getString(EXTRA_SORT_CONFIG, null)
			sortConfig = gson.fromJson(stringRepresentation, CatalogSortConfig::class.java)
		}
	}

	fun getActualSortingConfig() : CatalogSortConfig = sortConfig

	fun updateSortingConfig(newConfig: CatalogSortConfig) {
		sortConfig = newConfig
		updatePreferences(newConfig)
	}

	private fun updatePreferences(newConfig: CatalogSortConfig) {
		val serializedSortConfig = gson.toJson(newConfig)
		sharedPreferences.edit().putString(EXTRA_SORT_CONFIG, serializedSortConfig).apply()
	}
}