package ru.anarkh.acomics.catalog.model

import java.io.Serializable

data class CatalogComicsItem(
	val hyperLink: String,
	val previewImage: String, // todo может быть гифкой, проверить как с фреской работает
	val title: Title,
	val description: String,
	val rating: MPAARating,
	val lastUpdated: Long, // Эта штука приходит в формате 15241. о_о
	val totalPages: String,
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

enum class MPAARating {
    UNDEFINED, G, PG, PG_13, R, NC_17;

	companion object {
		fun fromString(rating: String?) : MPAARating {
			return when(rating) {
				"G" -> G
				"PG" -> PG
				"PG_13" -> PG_13
				"R" -> R
				"NC_17" -> NC_17
				else -> UNDEFINED
			}
		}
	}
}