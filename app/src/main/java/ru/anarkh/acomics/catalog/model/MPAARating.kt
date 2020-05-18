package ru.anarkh.acomics.catalog.model

import androidx.annotation.ColorRes

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