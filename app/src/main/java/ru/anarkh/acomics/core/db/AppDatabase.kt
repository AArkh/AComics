package ru.anarkh.acomics.core.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
	entities = [
		FavoriteEntity::class
	],
	version = 2
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun favoritesDao(): FavoritesDao
}