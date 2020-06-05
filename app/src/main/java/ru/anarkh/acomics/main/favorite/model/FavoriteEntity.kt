package ru.anarkh.acomics.main.favorite.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

const val FAVORITES_TABLE = "favorites"

@Entity(tableName = FAVORITES_TABLE)
data class FavoriteEntity(
	@field:PrimaryKey val catalogId: String,
	val previewImage: String,
	val totalPages: Int,
	val readPages: Int,
	val title: String,
	val description: String
) : Serializable