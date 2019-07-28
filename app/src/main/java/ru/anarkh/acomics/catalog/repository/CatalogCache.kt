package ru.anarkh.acomics.catalog.repository

import android.util.SparseArray
import androidx.core.util.contains
import androidx.lifecycle.ViewModel
import ru.anarkh.acomics.catalog.model.CatalogComicsItem

class CatalogCache : ViewModel() {

	private val cachedCatalogPages = SparseArray<List<CatalogComicsItem>>()

	fun putPage(pageIndex: Int, page: List<CatalogComicsItem>) {
		cachedCatalogPages.put(pageIndex, page)
	}

	fun getPage(pageIndex: Int) : List<CatalogComicsItem>? {
		return if (cachedCatalogPages.contains(pageIndex)) {
			cachedCatalogPages[pageIndex]
		} else null
	}

	fun invalidateCache() {
		cachedCatalogPages.clear()
	}
}