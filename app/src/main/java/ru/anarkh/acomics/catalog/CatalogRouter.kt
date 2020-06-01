package ru.anarkh.acomics.catalog

import android.content.Context
import ru.anarkh.acomics.comics.ComicsActivity

class CatalogRouter(private val context: Context) {

	fun openComicsPage(comicsTitle: String, pagesAmount: Int) {
		val intent = ComicsActivity.intent(context, comicsTitle, pagesAmount)
		context.startActivity(intent)
	}
}