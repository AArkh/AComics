package ru.anarkh.acomics.main.catalog

import android.content.Context
import ru.anarkh.acomics.comics.ComicsActivity

class CatalogRouter(private val context: Context) {

	fun openComicsPage(catalogId: String, pagesAmount: Int) {
		val intent = ComicsActivity.intent(context, catalogId, pagesAmount)
		context.startActivity(intent)
	}
}