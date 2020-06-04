package ru.anarkh.acomics.main.catalog.model

import java.io.Serializable

data class CatalogComicsItemWebModel(
	val catalogId: String,
	val previewImage: String,
	val title: String,
	val description: String,
	val rating: String,
	val lastUpdated: Long, // Эта штука приходит в формате 15241. В секундах.
	val totalPages: Int,
	val totalSubscribers: Int
) : Serializable