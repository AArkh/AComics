package ru.anarkh.acomics.catalog.repository

import android.net.Uri
import android.util.Log
import androidx.annotation.WorkerThread
import okhttp3.OkHttpClient
import okhttp3.Request

private const val CATALOG_URL = "https://acomics.ru/comics"
private const val CATALOG_SKIP_QUERY_PARAM = "skip" //todo По кол-ву подписчиков отфильтровать
// , а то капец часто обновляется по дате.

//https://acomics.ru/comics?categories=&ratings%5B%5D=2&ratings%5B%5D=3&ratings%5B%5D=4&ratings%5B%5D=5&type=trans&updatable=0&issue_count=2&sort=last_update

class Repository(
	private val httpClient: OkHttpClient,
	private val catalogParser: CatalogHTMLParser
) : CatalogRepository {

	@WorkerThread
	override fun getCatalog(currentItemsAmount: Int): List<CatalogComicsItem> {
		try {
			val uri = Uri.Builder()
				.encodedPath(CATALOG_URL)
				.appendQueryParameter(CATALOG_SKIP_QUERY_PARAM, currentItemsAmount.toString())
				.build()
			Log.d("12345", "uri is : $uri")

			val request = Request.Builder()
				.url(uri.toString())
				.build()
			val response = httpClient.newCall(request).execute()
			val body = response.body()?.string() ?: throw IllegalStateException(
				"response body is null for request: $uri"
			)

			val list = body.split("\n".toRegex())
			list.forEach {
				Log.d("12345", it)
			}

			return catalogParser.parse(body)

		} catch (e: Throwable) {
			e.printStackTrace()
			return emptyList()
		}
	}
}