package ru.anarkh.acomics.catalog.repository

import androidx.paging.PageKeyedDataSource
import ru.anarkh.acomics.core.state.SavedInt
import ru.arkharov.statemachine.StateRegistry

class CatalogDataSource(
	private val repository: CatalogRepository,
	private val sortConfigRepository: CatalogSortConfigRepository,
	stateRegistry: StateRegistry
) : PageKeyedDataSource<Int, Any>() {

	private val savedPosition = SavedInt("catalog_position", 0)

	init {
		stateRegistry.register(savedPosition)
	}

	override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Any>) {
		var initialList: List<Any> = repository.getCatalogPage(savedPosition.value)
		if (savedPosition.value == 0) {
			initialList = addConfigItem(initialList)
		}
		val prevPageKey = if (savedPosition.value == 0) null else savedPosition.value - 1
		callback.onResult(initialList, prevPageKey, savedPosition.value + 1)
	}

	override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Any>) {
		val itemList = repository.getCatalogPage(params.key)
		val nextPageKey = if (itemList.isNullOrEmpty()) null else params.key + 1
		callback.onResult(itemList, nextPageKey)
	}

	override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Any>) {
		var itemList: List<Any> = repository.getCatalogPage(params.key)
		val prevPageKey = if (params.key == 0) null else params.key - 1
		if (params.key == 0) {
			itemList = addConfigItem(itemList)
		}
		callback.onResult(itemList, prevPageKey)
	}

	private fun addConfigItem(listToBeDecorated: List<Any>): List<Any> {
		val list = ArrayList<Any>(listToBeDecorated.size + 1)
		list.add(sortConfigRepository.getActualSortingConfig())
		list.addAll(listToBeDecorated)
		return list
	}
}