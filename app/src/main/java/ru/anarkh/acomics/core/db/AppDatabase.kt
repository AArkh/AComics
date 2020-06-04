package ru.anarkh.acomics.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.anarkh.acomics.main.favorite.model.FavoriteEntity

@Database(entities = arrayOf(FavoriteEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
	abstract fun favoritesDao() : FavoritesDao
}