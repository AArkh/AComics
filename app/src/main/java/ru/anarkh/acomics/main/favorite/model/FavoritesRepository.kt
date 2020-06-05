package ru.anarkh.acomics.main.favorite.model

import androidx.annotation.WorkerThread
import ru.anarkh.acomics.core.db.FavoritesDao
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel

class FavoritesRepository(
	private val dao: FavoritesDao
) {

	@Synchronized
	@WorkerThread
	fun delete(catalogId: String) {
		dao.delete(catalogId)
	}

	/**
	 * @return Удаленную или добавленнную [FavoriteEntity]
	 */
	@WorkerThread
	@Synchronized
	fun toggleFavorite(model: CatalogComicsItemWebModel): FavoriteEntity {
		var entry = dao.findById(model.catalogId)
		if (entry != null) {
			dao.delete(entry)
		} else {
			entry = FavoriteEntity(
				model.catalogId,
				model.previewImage,
				model.totalPages,
				0,
				model.title,
				model.description
			)
			dao.insert(entry)
		}
		return entry
	}

	@WorkerThread
	@Synchronized
	fun getFavoriteIds(): List<String> {
		return getFavorites().map { entity: FavoriteEntity ->
			return@map entity.catalogId
		}
	}

	@WorkerThread
	@Synchronized
	fun getFavorites(): List<FavoriteEntity> {
		return dao.getAll()
	}

	@WorkerThread
	@Synchronized
	fun update(model: CatalogComicsItemWebModel) {
		val currentEntry: FavoriteEntity = dao.findById(model.catalogId) ?: return
		val newEntry = currentEntry.copy(
			title = model.title,
			description = model.description,
			totalPages = model.totalPages,
			previewImage = model.previewImage
		)
		dao.insert(newEntry)
	}
}