package ru.anarkh.acomics.catalog.repository

import android.util.Log
import androidx.annotation.WorkerThread
import retrofit2.Response
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.catalog.model.CatalogSortingBy
import ru.anarkh.acomics.core.api.AComicsCatalogService

class CatalogDataSource(
	private val catalogService: AComicsCatalogService
) {

	@Throws
	@WorkerThread
	fun loadInitial(sortConfig: CatalogSortConfig, page: Int): List<CatalogComicsItem> {
		val isAsc = sortConfig.sorting == CatalogSortingBy.BY_ALPHABET
			|| sortConfig.sorting == CatalogSortingBy.BY_DATE
		val response: Response<List<CatalogComicsItem>> = catalogService.catalogList(
			sortConfig.sorting.queryParamValue,
			isAsc,
			page,
			sortConfig.rating.mapTo(ArrayList(sortConfig.rating.size), { return@mapTo it.text })
		).execute()
		Log.d("12345", "response is $response")
		return response.body() ?: throw Exception()
	}
}