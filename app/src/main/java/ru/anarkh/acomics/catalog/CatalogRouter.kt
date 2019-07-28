package ru.anarkh.acomics.catalog

import android.content.Context
import ru.anarkh.acomics.comics.ComicsActivity

class CatalogRouter(private val context: Context) {

	fun openComicsPage(comicsLink: String, pagesAmount: Int) {
		val intent = ComicsActivity.intent(context, comicsLink, pagesAmount)
		context.startActivity(intent)
	}
}