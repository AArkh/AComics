package ru.anarkh.acomics.catalog.repository

import androidx.annotation.WorkerThread
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.model.CatalogSortConfig

class CatalogRepository(
	private val dataSource: CatalogDataSource // Добавить кэш
) {

	@WorkerThread
	fun getList(sortConfig: CatalogSortConfig, page: Int) : List<CatalogComicsItem> {
		try {
			return dataSource.loadInitial(sortConfig, page)
		} catch (e: Exception) {
			e.printStackTrace()
			throw e
		}
	}
}