package ru.anarkh.acomics.catalog.repository

import androidx.paging.PageKeyedDataSource
import ru.anarkh.acomics.catalog.model.CatalogComicsItem

class CatalogDataSource(
	private val repository: CatalogRepository
) : PageKeyedDataSource<Int, CatalogComicsItem>() {

	override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, CatalogComicsItem>) {
		val initialList = repository.getCatalogPage(0)
		callback.onResult(initialList, null, 1)
	}

	override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, CatalogComicsItem>) {
		val itemList = repository.getCatalogPage(params.key)
		val nextPageKey = if (itemList.isNullOrEmpty()) null else params.key + 1
		callback.onResult(itemList, nextPageKey)
	}

	override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, CatalogComicsItem>) {
		val itemList = repository.getCatalogPage(params.key)
		val prevPageKey = if (params.key == 0) null else params.key - 1
		callback.onResult(itemList, prevPageKey)
	}
}