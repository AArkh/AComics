package ru.anarkh.acomics.catalog.model

import androidx.annotation.ColorRes

enum class MPAARating(
	val queryParamValue: String,
	val text: String,
	@ColorRes val colorRes: Int
) {
	UNDEFINED("1", "NR", ru.anarkh.acomics.R.color.rating_undef),
	G("2", "G", ru.anarkh.acomics.R.color.rating_g),
	PG("3", "PG", ru.anarkh.acomics.R.color.rating_pg),
	PG_13("4", "PG-13", ru.anarkh.acomics.R.color.rating_pg13),
	R("5", "R", ru.anarkh.acomics.R.color.rating_r),
	NC_17("6", "NC-17", ru.anarkh.acomics.R.color.rating_nc17);

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