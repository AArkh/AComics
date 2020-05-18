package ru.anarkh.acomics.catalog.controller

import ru.anarkh.acomics.catalog.CatalogRouter
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.catalog.model.CatalogSortingBy
import ru.anarkh.acomics.catalog.repository.CatalogRepository
import ru.anarkh.acomics.catalog.repository.CatalogSortConfigRepository
import ru.anarkh.acomics.catalog.widget.CatalogWidget
import ru.anarkh.acomics.catalog.widget.filter.CatalogFilterDialogWidget
import ru.anarkh.acomics.catalog.widget.filter.CatalogSortDialogWidget
import ru.anarkh.acomics.catalog.widget.paging.*
import ru.anarkh.acomics.core.coroutines.ActivityScope
import ru.anarkh.acomics.core.coroutines.ObserverBuilder
import ru.arkharov.statemachine.SavedSerializable
import ru.arkharov.statemachine.StateRegistry
import java.io.Serializable

private const val BUNDLE_KEY = "catalog_controller_bundle_key"
private const val INITIAL_TASK_KEY = "catalog_controller_initial"
private const val PAGINATION_TASK_KEY = "catalog_controller_pagination"

class CatalogController(
	private val router: CatalogRouter,
	private val widget: CatalogWidget,
	private val sortDialogWidget: CatalogSortDialogWidget,
	private val filterDialogWidget: CatalogFilterDialogWidget,
	private val sortConfigRepository: CatalogSortConfigRepository,
	private val catalogRepository: CatalogRepository,
	private val activityScope: ActivityScope,
	stateRegistry: StateRegistry
) {

	private val savedState = SavedSerializable<PagingState>(BUNDLE_KEY, Initial)

	init {
		stateRegistry.register(savedState)
		initWidget()
		initDialogs()
		initAsyncObservers()
		updateList()
	}

	private fun initWidget() {
		widget.onComicsClick { link: String, pagesAmount: Int ->
			router.openComicsPage(link, pagesAmount)
		}
		widget.onSortIconClick {
			sortDialogWidget.show()
		}
		widget.onFilterItemClick {
			filterDialogWidget.show()
		}
		widget.onLoadingElementReached = {
			val currentState: PagingState? = savedState.value
			if (currentState is Content && currentState.state == Content.ContentState.HAS_MORE) {
				savedState.value = currentState.copy(state = Content.ContentState.LOADING_NEXT_PAGE)
				loadNextPage(currentState.page.inc())
			}
		}
	}

	private fun initDialogs() {
		sortDialogWidget.currentlyPickedSortingProvider = {
			sortConfigRepository.getActualSortingConfig().sorting
		}
		sortDialogWidget.onSortingItemClick = { pickedSort: CatalogSortingBy ->
			val currentCatalogConfig = sortConfigRepository.getActualSortingConfig()
			if (pickedSort != currentCatalogConfig.sorting) {
				currentCatalogConfig.sorting = pickedSort
				updateFilter(currentCatalogConfig)
			}
		}
		sortDialogWidget.retain()
		filterDialogWidget.currentFilterConfigProvider = {
			sortConfigRepository.getActualSortingConfig()
		}
		filterDialogWidget.onDialogCloseListener = { dialogConfigChanges: CatalogSortConfig ->
			updateFilter(dialogConfigChanges)
		}
		filterDialogWidget.retain()
	}

	private fun updateFilter(newConfig: CatalogSortConfig) {
		sortConfigRepository.updateSortingConfig(newConfig)
		savedState.value = Initial
		updateList()
	}

	private fun initAsyncObservers() {
		var observer = ObserverBuilder<List<Serializable>>(INITIAL_TASK_KEY)
			.onFailed {
				savedState.value = Failed
				updateList()
			}
			.onLoading {
				savedState.value = Loading
				updateList()
			}
			.onSuccess { list: List<Serializable> ->
				val contentState =
					if (list.size >= 20) Content.ContentState.HAS_MORE
					else Content.ContentState.END_REACHED
				val state = Content(list, 1, contentState)
				savedState.value = state
				updateList()
			}
			.build()
		activityScope.addObserver(observer)
		observer = ObserverBuilder<List<Serializable>>(PAGINATION_TASK_KEY)
			.onFailed {
				val currentState: Content = savedState.value as? Content ?: return@onFailed
				savedState.value = currentState.copy(state = Content.ContentState.FAILED)
				updateList()
			}
			.onSuccess { list: List<Serializable> ->
				val currentState = savedState.value as? Content ?: return@onSuccess
				val newList = currentState.resultList + list
				val page = currentState.page.inc()
				val contentState =
					if (list.size >= 20) Content.ContentState.HAS_MORE
					else Content.ContentState.END_REACHED
				savedState.value = Content(newList, page, contentState)
				updateList()
			}
			.build()
		activityScope.addObserver(observer)
	}

	private fun updateList() {
		val currentState = savedState.value ?: Initial
		widget.updateState(currentState)
		if (savedState.value is Initial) {
			activityScope.asyncObservable(INITIAL_TASK_KEY) {
				val config = sortConfigRepository.getActualSortingConfig()
				val catalogPage: List<CatalogComicsItem> = catalogRepository.getList(config, 1)
				val resultedList: MutableList<Any> = ArrayList(catalogPage)
				resultedList.add(0, config)
				return@asyncObservable resultedList
			}
		}
	}

	private fun loadNextPage(page: Int) {
		activityScope.asyncObservable(PAGINATION_TASK_KEY) {
			val config = sortConfigRepository.getActualSortingConfig()
			return@asyncObservable catalogRepository.getList(config, page)
		}
	}
}