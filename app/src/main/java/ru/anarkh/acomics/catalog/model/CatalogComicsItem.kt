package ru.anarkh.acomics.catalog.model

import androidx.annotation.ColorRes
import java.io.Serializable

data class CatalogComicsItem(
	val hyperLink: String,
	val previewImage: String,
	val title: Title,
	val description: String,
	val rating: MPAARating,
	val lastUpdated: Long, // Эта штука приходит в формате 15241. о_о
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

enum class MPAARating(
	val text: String,
	@ColorRes val colorRes: Int
) {
    UNDEFINED("NR", ru.anarkh.acomics.R.color.rating_undef),
	G("G", ru.anarkh.acomics.R.color.rating_g),
	PG("PG", ru.anarkh.acomics.R.color.rating_pg),
	PG_13("PG-13", ru.anarkh.acomics.R.color.rating_pg13),
	R("R", ru.anarkh.acomics.R.color.rating_r),
	NC_17("NC-17", ru.anarkh.acomics.R.color.rating_nc17);

	companion object {
		fun fromString(rating: String?) : MPAARating {
			return when(rating) {
				"G" -> G
				"PG" -> PG
				"PG-13" -> PG_13
				"R" -> R
				"NC-17" -> NC_17
				else -> UNDEFINED
			}
		}
	}
}