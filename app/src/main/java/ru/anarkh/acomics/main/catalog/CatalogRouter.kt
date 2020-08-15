package ru.anarkh.acomics.main.catalog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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

	fun openReport(comicsTitleToBeReported: String) {
		val intent = Intent(Intent.ACTION_SENDTO)
		intent.setData(Uri.parse("mailto:admin@acomics.ru"))
			.putExtra(Intent.EXTRA_EMAIL, arrayOf("admin@acomics.ru"))
			.putExtra(Intent.EXTRA_SUBJECT, "Acomics mobile app content report")
			.putExtra(Intent.EXTRA_TEXT, "Hi!\nI want to report a comics $comicsTitleToBeReported.")
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		try {
			fragment.startActivity(Intent.createChooser(intent, "Send email..."))
		} catch (e: Exception) {
			Toast.makeText(
				fragment.requireContext(),
				"Couldn't find an email app",
				Toast.LENGTH_SHORT
			).show()
		}
	}
}