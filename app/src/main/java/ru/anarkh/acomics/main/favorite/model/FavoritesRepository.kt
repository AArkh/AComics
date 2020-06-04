package ru.anarkh.acomics.main.favorite.model

import ru.anarkh.acomics.core.db.FavoritesDao

class FavoritesRepository(
	private val dao: FavoritesDao
) {

	@Synchronized
	fun toggleFavorite(catalogId: String) {
		val entry = dao.findById(catalogId)
		if (entry != null) {
			dao.delete(entry)
		} else {
			dao.insert(FavoriteEntity(catalogId))
		}
	}

	@Synchronized
	fun getFavoriteIds(): List<String> {
		return dao.getAll().map { entity: FavoriteEntity ->
			return@map entity.catalogId
		}
	}
}