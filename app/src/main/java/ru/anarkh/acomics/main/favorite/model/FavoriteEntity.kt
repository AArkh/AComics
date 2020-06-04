package ru.anarkh.acomics.main.favorite.model

import androidx.room.Entity
import androidx.room.PrimaryKey

const val FAVORITE_TABLE = "favorites"

@Entity(tableName = FAVORITE_TABLE)
data class FavoriteEntity(
	@PrimaryKey val catalogId: String
)