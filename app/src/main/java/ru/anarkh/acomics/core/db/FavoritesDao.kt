package ru.anarkh.acomics.core.db

import androidx.annotation.WorkerThread
import androidx.room.*
import ru.anarkh.acomics.main.favorite.model.FAVORITES_TABLE
import ru.anarkh.acomics.main.favorite.model.FavoriteEntity

@Dao
interface FavoritesDao {

	@WorkerThread
	@Query("SELECT * FROM $FAVORITES_TABLE")
	fun getAll(): List<FavoriteEntity>

	@WorkerThread
	@Query("SELECT * FROM $FAVORITES_TABLE WHERE catalogId = :catalogId")
	fun findById(catalogId: String): FavoriteEntity?

	@WorkerThread
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(favorites: FavoriteEntity)

	@WorkerThread
	@Delete
	fun delete(favorite: FavoriteEntity)

	@WorkerThread
	@Query("DELETE FROM $FAVORITES_TABLE WHERE catalogId = :catalogId")
	fun delete(catalogId: String)
}