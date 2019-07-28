package ru.anarkh.acomics.catalog.repository

import androidx.annotation.WorkerThread
import ru.anarkh.acomics.catalog.model.CatalogComicsItem

/**
 * На всякий случай контракт, вдруг API появится?
 */
interface CatalogRepository {
	/**
	 * [catalogPageIndex] количество элементов в каталоге для ленивой подгрузки
	 */
	@WorkerThread
	fun getCatalogPage(catalogPageIndex: Int) : List<CatalogComicsItem>

	fun invalidateCache()
}