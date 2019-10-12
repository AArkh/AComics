package ru.anarkh.acomics.catalog.repository

import android.net.Uri
import androidx.annotation.WorkerThread
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import java.io.InterruptedIOException
import java.text.ParseException

private const val CATALOG_URL = "https://acomics.ru/comics"
private const val CATALOG_SKIP_QUERY_PARAM = "skip"
private const val CATALOG_SORT_QUERY_PARAM = "sort"
private const val CATALOG_TRANSLATION_TYPE_QUERY_PARAM = "type"
private const val CATALOG_RATING_QUERY_PARAM = "ratings"
private const val ACOMICS_DEFAULT_PAGINATION_OFFSET = 10 // Не умеет страничка иначе, поэтому не кастомизируем

class CatalogRepositoryImpl(
	private val httpClient: OkHttpClient,
	private val catalogParser: CatalogHTMLParser,
	private val catalogPagesLocalCache: CatalogCache,
	private var catalogSortConfigRepository: CatalogSortConfigRepository
) : CatalogRepository {

	/**
	 * @throws InterruptedIOException ежели нет сети,
	 * @throws ParseException если сломался парсинг.
	 */
	@WorkerThread
	@Throws(InterruptedIOException::class, ParseException::class, IllegalStateException::class)
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
	override fun invalidateCache() {
		catalogPagesLocalCache.invalidateCache()
	}

	private fun retrieveFromServer(itemsAmountToSkip: Int) : List<CatalogComicsItem> {
		val sortConfig = catalogSortConfigRepository.getActualSortingConfig()
		val uri = Uri.Builder()
			.encodedPath(CATALOG_URL)
			.appendQueryParameterArray(
				CATALOG_RATING_QUERY_PARAM,
				sortConfig.rating.map { it.queryParamValue }
			)
			.appendQueryParameter(
				CATALOG_TRANSLATION_TYPE_QUERY_PARAM,
				sortConfig.translationType.queryParam
			)
			.appendQueryParameter(
				CATALOG_SORT_QUERY_PARAM,
				sortConfig.sorting.queryParamValue
			)
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

	private fun Uri.Builder.appendQueryParameterArray(key: String, values: Iterable<String>): Uri.Builder {
		values.forEach {
			appendQueryParameter("$key[]", it)
		}
		return this
	}
}