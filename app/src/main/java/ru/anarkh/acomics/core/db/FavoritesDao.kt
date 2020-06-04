package ru.anarkh.acomics.core.db

import androidx.annotation.WorkerThread
import androidx.room.*
import ru.anarkh.acomics.main.favorite.model.FAVORITE_TABLE
import ru.anarkh.acomics.main.favorite.model.FavoriteEntity

@Dao
interface FavoritesDao {

	@WorkerThread
	@Query("SELECT * FROM $FAVORITE_TABLE")
	fun getAll(): List<FavoriteEntity>

	@WorkerThread
	@Query("SELECT * FROM $FAVORITE_TABLE WHERE catalogId = :catalogId")
	fun findById(catalogId: String): FavoriteEntity?

	@WorkerThread
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(favorites: FavoriteEntity)

	@WorkerThread
	@Delete
	fun delete(favorite: FavoriteEntity)
}