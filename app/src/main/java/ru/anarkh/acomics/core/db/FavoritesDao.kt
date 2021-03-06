package ru.anarkh.acomics.core.db

import androidx.annotation.WorkerThread
import androidx.room.*

@Dao
interface FavoritesDao {

	@WorkerThread
	@Query("SELECT * FROM $FAVORITES_TABLE order by catalogId")
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