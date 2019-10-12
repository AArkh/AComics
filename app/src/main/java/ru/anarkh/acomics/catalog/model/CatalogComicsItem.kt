package ru.anarkh.acomics.catalog.model

import java.io.Serializable

data class CatalogComicsItem(
	val hyperLink: String,
	val previewImage: String,
	val title: Title,
	val description: String,
	val rating: MPAARating,
	val lastUpdated: Long, // Эта штука приходит в формате 15241.
	val totalPages: Int,
	val formattedTotalPages: String, // формата "232 выпуска"
	val ongoningRate: Double, // Коэффициент(выпуска в месяц?) странный, приходит в формате 4.778
	val totalSubscribers: Int
) : Serializable

data class Title(
    val comicsTitle: String,
    val catalogIconsList: List<Icon>
) : Serializable

enum class Icon : Serializable {
    TRANSLATED, RECOMMENDED, VOTED
    //todo Добавить drawableRes в конструктор
}