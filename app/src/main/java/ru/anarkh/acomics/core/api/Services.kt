package ru.anarkh.acomics.core.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.comics.model.ComicsPage


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
	): Call<List<CatalogComicsItem>>
}

interface AComicsIssuesService {
	@GET("/comics/{name}")
	fun issue(@Path("name") comicsName: String): Call<List<ComicsPage>>
}