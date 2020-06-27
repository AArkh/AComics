package ru.anarkh.acomics.main.catalog.repository

import androidx.annotation.WorkerThread
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemUiModel
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel
import ru.anarkh.acomics.main.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.main.catalog.model.NoThanks
import ru.anarkh.acomics.main.favorite.model.FavoritesRepository

class CatalogRepository(
	private val dataSource: CatalogDataSource, // Добавить кэш
	private val favoritesRepository: FavoritesRepository
) {

	@Synchronized
	@WorkerThread
	fun getList(sortConfig: CatalogSortConfig, page: Int): List<CatalogComicsItemUiModel> {
		try {
			val rawList: List<CatalogComicsItemWebModel> = dataSource.loadCatalog(sortConfig, page)
			val favoriteIds: List<String> = favoritesRepository.getFavoriteIds()
			return mapToUiModelList(rawList, favoriteIds)
		} catch (e: Exception) {
			e.printStackTrace()
			throw e
		}
	}

	@Synchronized
	@WorkerThread
	fun search(mask: String): List<CatalogComicsItemUiModel> {
		try {
			val rawList: List<CatalogComicsItemWebModel> = dataSource.search(mask)
			val favoriteIds: List<String> = favoritesRepository.getFavoriteIds()
			return mapToUiModelList(rawList, favoriteIds)
		} catch (e: Exception) {
			e.printStackTrace()
			throw e
		}
	}

	private fun mapToUiModelList(
		rawList: List<CatalogComicsItemWebModel>,
		favoriteIds: List<String>
	): List<CatalogComicsItemUiModel> {
		return rawList.map { webModel: CatalogComicsItemWebModel ->
			val isFavorite = favoriteIds.contains(webModel.catalogId)
			if (isFavorite) {
				favoritesRepository.update(webModel)
			}
			return@map CatalogComicsItemUiModel(
				webModel,
				isFavorite,
				NoThanks
			)
		}
	}
}