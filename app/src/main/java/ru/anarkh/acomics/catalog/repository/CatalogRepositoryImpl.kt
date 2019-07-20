package ru.anarkh.acomics.catalog.repository

import android.net.Uri
import androidx.annotation.WorkerThread
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import java.io.InterruptedIOException
import java.text.ParseException

private const val CATALOG_URL = "https://acomics.ru/comics"
private const val CATALOG_SKIP_QUERY_PARAM = "skip" //todo По кол-ву подписчиков отфильтровать
// , а то капец часто обновляется по дате.
private const val ACOMICS_DEFAULT_PAGINATION_OFFSET = 10 // Не умеет страничка иначе, поэтому не кастомизируем

//https://acomics.ru/comics?categories=&ratings%5B%5D=2&ratings%5B%5D=3&ratings%5B%5D=4&ratings%5B%5D=5&type=trans&updatable=0&issue_count=2&sort=last_update

class CatalogRepositoryImpl(
	private val httpClient: OkHttpClient,
	private val catalogParser: CatalogHTMLParser,
	private val catalogPagesLocalCache: CatalogCache
) : CatalogRepository {

	/**
	 * @throws InterruptedIOException ежели нет сети,
	 * @throws ParseException если сломался парсинг.
	 */
	@WorkerThread
	@Throws(InterruptedIOException::class, ParseException::class)
	override fun getCatalogPage(catalogPageIndex: Int): List<CatalogComicsItem> {
		val cached = catalogPagesLocalCache.getPage(catalogPageIndex)
		if (cached != null) {
			return cached
		}
		val itemsAmountToSkip = catalogPageIndex * ACOMICS_DEFAULT_PAGINATION_OFFSET
		val newPage = retrieveFromServer(itemsAmountToSkip)
		catalogPagesLocalCache.putPage(catalogPageIndex, newPage)
		return newPage
	}

	private fun retrieveFromServer(itemsAmountToSkip: Int) : List<CatalogComicsItem> {
		val uri = Uri.Builder()
			.encodedPath(CATALOG_URL)
			.appendQueryParameter(
				CATALOG_SKIP_QUERY_PARAM,
				itemsAmountToSkip.toString()
			)
			.build()
		val request = Request.Builder()
			.url(uri.toString())
			.build()
		val response = httpClient.newCall(request).execute()
		val body = response.body()?.string() ?: throw IllegalStateException(
			"response body is null for request: $uri"
		)
		return catalogParser.parse(body)
	}
}