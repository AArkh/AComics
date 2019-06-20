package ru.anarkh.acomics.catalog.repository

import android.util.Log
import org.jsoup.Jsoup

private const val CATALOG_TABLE_ELEMENT_CLASS_NAME = "catalog-elem list-loadable"
private const val CATALOG_ELEMENT_PREVIEW_IMAGE_CLASS_NAME = "catdata1"
private const val CATALOG_ELEMENT_DESCRIPTION_CLASS_NAME = "catdata2"
private const val CATALOG_ELEMENT_ACTIVITY_CLASS_NAME = "catdata3"
private const val CATALOG_ELEMENT_SUBSCRIBERS_CLASS_NAME = "catdata4"

class CatalogHTMLParser {

	fun parse(html: String): List<CatalogComicsItem> {
		val doc = Jsoup.parse(html)
		val elements = doc.getElementsByClass(CATALOG_TABLE_ELEMENT_CLASS_NAME)
		Log.d("12345", "elements size is ${elements.size}")

		val catalogList = ArrayList<CatalogComicsItem>(elements.size)

		elements.forEach {
			val htmlString = it.child(0)
			val previewElement = htmlString.getElementsByClass(CATALOG_ELEMENT_PREVIEW_IMAGE_CLASS_NAME)
				.first()
				.child(0)
			val comicLink = previewElement.attr("href")
			val imageLink = previewElement.getElementsByTag("img")
				.attr("src")

			val descriptionElement = htmlString.getElementsByClass(CATALOG_ELEMENT_DESCRIPTION_CLASS_NAME)
				.first()
			val title = descriptionElement.getElementsByClass("title")
				.first()
				.child(0)
				.text()
			val description = descriptionElement.getElementsByClass("about")
				.first()
				.text()
			val rating = descriptionElement.getElementsByClass("also")
				.first()
				.getElementsByTag("a")
				.first()
				.text()

			val activityElement = htmlString.getElementsByClass(CATALOG_ELEMENT_ACTIVITY_CLASS_NAME)
				.first()
			val lastUpdate = activityElement.getElementsByClass("time")
				.first()
				.text()
				.replace("=", "")
				.toLong()
			val totalPages = activityElement.getElementsByClass("total")
				.first()
				.text()

			val subscribersElement = htmlString.getElementsByClass(CATALOG_ELEMENT_SUBSCRIBERS_CLASS_NAME)
				.first()
			val subsCount = subscribersElement.getElementsByClass("subscribe")
				.first()
				.child(0)
				.text()
				.toInt()

			val catalogItem = CatalogComicsItem(
				comicLink,
				imageLink,
				Title(title, emptyList()),
				description,
				MPAARating.fromString(rating),
				lastUpdate,
				totalPages,
				0.0,
				subsCount
			)
			catalogList.add(catalogItem)
		}
		return catalogList
	}
}