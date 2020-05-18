package ru.anarkh.acomics.catalog.model

import java.io.Serializable

data class CatalogComicsItem(
	val previewImage: String,
	val title: String,
	val description: String,
	val rating: String,
	val lastUpdated: Long, // Эта штука приходит в формате 15241. В секундах.
	val totalPages: Int,
	val totalSubscribers: Int
) : Serializable