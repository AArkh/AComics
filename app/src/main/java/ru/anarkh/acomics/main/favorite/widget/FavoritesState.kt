package ru.anarkh.acomics.main.favorite.widget

import ru.anarkh.acomics.core.db.FavoriteEntity
import java.io.Serializable

sealed class FavoritesState : Serializable

object Initial: FavoritesState()
object NoSavedFavorites: FavoritesState()
object Failed: FavoritesState()
object Loading: FavoritesState()

data class Content(
	val favorites: List<FavoriteEntity>
): FavoritesState(), Serializable