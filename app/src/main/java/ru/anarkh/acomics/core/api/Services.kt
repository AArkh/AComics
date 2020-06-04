package ru.anarkh.acomics.core.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.anarkh.acomics.comics.model.ComicsPageModel
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel


interface AComicsCatalogService {
	@GET("/search")
	fun catalogList(
		@Query("sortingBy")
		sortingBy: String,
		@Query("isAsc")
		isAsc: Boolean,
		@Query("page")
		page: Int,
		@Query("rating")
		rating: List<String>
	): Call<List<CatalogComicsItemWebModel>>
}

interface AComicsIssuesService {
	@GET("/comics/{id}")
	fun issue(@Path("id") catalogId: String): Call<List<ComicsPageModel>>
}