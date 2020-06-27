package ru.anarkh.acomics.main.catalog

import android.app.Activity
import android.content.Intent
import ru.anarkh.acomics.comics.CATALOG_ID_EXTRA
import ru.anarkh.acomics.comics.ComicsActivity
import ru.anarkh.acomics.core.DefaultFragment
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel

private const val START_COMICS_ACTIVITY_CODE = 10

class CatalogRouter(private val fragment: DefaultFragment) {

	fun openComicsPage(catalogId: String, pagesAmount: Int) {
		val intent = ComicsActivity.intent(fragment.requireContext(), catalogId, pagesAmount)
		fragment.startActivityForResult(intent, START_COMICS_ACTIVITY_CODE)
	}

	fun openComicsPage(catalogComicsItemWebModel: CatalogComicsItemWebModel) {
		val intent = ComicsActivity.intent(fragment.requireContext(), catalogComicsItemWebModel)
		fragment.startActivityForResult(intent, START_COMICS_ACTIVITY_CODE)
	}

	fun setOnComicsScreenReturnCallback(callback: (catalogId: String) -> Unit) {
		fragment.activityResultObservers.put(
			START_COMICS_ACTIVITY_CODE,
			{ resultCode: Int, data: Intent? ->
				if (resultCode == Activity.RESULT_OK && data != null) {
					val catalogId: String? = data.getStringExtra(CATALOG_ID_EXTRA)
					catalogId?.let(callback)
				}
				true
			}
		)
	}
}