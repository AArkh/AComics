package ru.anarkh.acomics.main.catalog.repository

import android.util.Log
import androidx.annotation.WorkerThread
import retrofit2.Response
import ru.anarkh.acomics.core.api.AComicsCatalogService
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel
import ru.anarkh.acomics.main.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.main.catalog.model.CatalogSortingBy

class CatalogDataSource(
	private val catalogService: AComicsCatalogService
) {

	@Throws
	@WorkerThread
	fun loadInitial(sortConfig: CatalogSortConfig, page: Int): List<CatalogComicsItemWebModel> {
		val isAsc = sortConfig.sorting == CatalogSortingBy.BY_ALPHABET
			|| sortConfig.sorting == CatalogSortingBy.BY_DATE
		val response: Response<List<CatalogComicsItemWebModel>> = catalogService.catalogList(
			sortConfig.sorting.queryParamValue,
			isAsc,
			page,
			sortConfig.rating.mapTo(ArrayList(sortConfig.rating.size), { return@mapTo it.text }),
			if (sortConfig.minPages > 0) sortConfig.minPages else null
		).execute()
		Log.d("12345", "response is $response")
		return response.body() ?: throw Exception()
	}
}