package ru.anarkh.acomics.catalog.repository

/**
 * На всякий случай контракт, вдруг API появится?
 */
interface CatalogRepository {
	/**
	 * [currentItemsAmount] количество элементов в каталоге для ленивой подгрузки
	 */
	fun getCatalog(currentItemsAmount: Int = 0): List<CatalogComicsItem>
}