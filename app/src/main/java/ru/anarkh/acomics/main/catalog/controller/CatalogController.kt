package ru.anarkh.acomics.main.catalog.controller

import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.anarkh.acomics.core.coroutines.ObservableScope
import ru.anarkh.acomics.core.coroutines.ObserverBuilder
import ru.anarkh.acomics.main.catalog.CatalogRouter
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemUiModel
import ru.anarkh.acomics.main.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.main.catalog.model.CatalogSortingBy
import ru.anarkh.acomics.main.catalog.repository.CatalogRepository
import ru.anarkh.acomics.main.catalog.repository.CatalogSortConfigRepository
import ru.anarkh.acomics.main.catalog.widget.CatalogWidget
import ru.anarkh.acomics.main.catalog.widget.filter.CatalogFilterDialogWidget
import ru.anarkh.acomics.main.catalog.widget.filter.CatalogSortDialogWidget
import ru.anarkh.acomics.main.catalog.widget.paging.*
import ru.anarkh.acomics.main.favorite.controller.REMOVE_FROM_FAVORITES_KEY
import ru.anarkh.acomics.main.favorite.model.FavoritesRepository
import ru.arkharov.statemachine.SavedSerializable
import ru.arkharov.statemachine.StateRegistry
import java.io.Serializable
import java.net.ConnectException

const val TOGGLE_FAVORITE_KEY = "toggle_favorite_key"

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
	private val favoritesRepository: FavoritesRepository,
	private val coroutineScope: ObservableScope,
	private val crashlytics: FirebaseCrashlytics,
	stateRegistry: StateRegistry
) {

	private val savedState = SavedSerializable<PagingState>(BUNDLE_KEY, Initial)

	init {
		stateRegistry.register(savedState)
		initWidget()
		initDialogs()
		initAsyncObservers()
		updateUi()
	}

	private fun initWidget() {
		widget.onComicsClick { catalogId: String, pagesAmount: Int ->
			router.openComicsPage(catalogId, pagesAmount)
		}
		widget.onAddToFavoritesClick { catalogId: String ->
			toggleFavorite(catalogId)
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
		widget.onRetryButtonClick {
			savedState.value = Initial
			updateUi()
		}
		widget.onRetryLoadNextPageButtonClick {
			val currentState: PagingState? = savedState.value
			if (currentState is Content) {
				savedState.value = currentState.copy(state = Content.ContentState.LOADING_NEXT_PAGE)
				updateUi()
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
			val currentConfig: CatalogSortConfig = sortConfigRepository.getActualSortingConfig()
			if (dialogConfigChanges != currentConfig) {
				updateFilter(dialogConfigChanges)
			}
		}
		filterDialogWidget.retain()
	}

	private fun updateFilter(newConfig: CatalogSortConfig) {
		sortConfigRepository.updateSortingConfig(newConfig)
		savedState.value = Initial
		updateUi()
	}

	private fun initAsyncObservers() {
		var observer = ObserverBuilder<List<Serializable>>(INITIAL_TASK_KEY)
			.onFailed {
				if (it !is ConnectException) {
					crashlytics.recordException(it)
				}
				savedState.value = Failed
				updateUi()
			}
			.onLoading {
				savedState.value = Loading
				updateUi()
			}
			.onSuccess { list: List<Serializable> ->
				val contentState =
					if (list.size >= 20) Content.ContentState.HAS_MORE
					else Content.ContentState.END_REACHED
				val state = Content(list, 1, contentState)
				savedState.value = state
				updateUi()
			}
			.build()
		coroutineScope.addObserver(observer)
		observer = ObserverBuilder<List<Serializable>>(PAGINATION_TASK_KEY)
			.onFailed {
				if (it !is ConnectException) {
					crashlytics.recordException(it)
				}
				val currentState: Content = savedState.value as? Content ?: return@onFailed
				savedState.value = currentState.copy(state = Content.ContentState.FAILED)
				updateUi()
			}
			.onSuccess { list: List<Serializable> ->
				val currentState: Content = savedState.value as? Content ?: return@onSuccess
				val newList: List<Serializable> = currentState.resultList + list
				val page: Int = currentState.page.inc()
				val contentState: Content.ContentState =
					if (list.size >= 20) Content.ContentState.HAS_MORE
					else Content.ContentState.END_REACHED
				savedState.value = Content(newList, page, contentState)
				updateUi()
			}
			.build()
		coroutineScope.addObserver(observer)
		val removeFavoriteObserver = ObserverBuilder<String>(REMOVE_FROM_FAVORITES_KEY)
			.onSuccess { catalogId: String ->
				val currentState = savedState.value as? Content ?: return@onSuccess
				val currentList = currentState.resultList
				val newList = currentList.map {
					return@map if (it is CatalogComicsItemUiModel && it.catalogId == catalogId) {
						return@map it.copy(isFavorite = false)
					} else it
				}
				savedState.value = currentState.copy(resultList = newList)
				updateUi()
			}
			.build()
		coroutineScope.addObserver(removeFavoriteObserver)
	}

	private fun updateUi() {
		val currentState = savedState.value ?: Initial
		widget.updateState(currentState)
		if (savedState.value is Initial) {
			coroutineScope.runObservable(INITIAL_TASK_KEY) {
				val config = sortConfigRepository.getActualSortingConfig()
				val catalogPage: List<CatalogComicsItemUiModel> =
					catalogRepository.getList(config, 1)
				val resultedList: MutableList<Any> = ArrayList(catalogPage)
				resultedList.add(0, config)
				return@runObservable resultedList
			}
		}
	}

	private fun loadNextPage(page: Int) {
		coroutineScope.runObservable(PAGINATION_TASK_KEY) {
			val config = sortConfigRepository.getActualSortingConfig()
			return@runObservable catalogRepository.getList(config, page)
		}
	}

	private fun toggleFavorite(catalogId: String) {
		val currentContent = savedState.value as? Content ?: return
		val list: List<Serializable> = currentContent.resultList
		val clickedIndex = list.indexOfFirst {
			if (it is CatalogComicsItemUiModel) {
				return@indexOfFirst it.catalogId == catalogId
			}
			return@indexOfFirst false
		}
		if (clickedIndex < 0) {
			return
		}
		val newList = ArrayList(list)
		val updatedItem = list[clickedIndex]
			as? CatalogComicsItemUiModel
			?: return
		newList.removeAt(clickedIndex)
		newList.add(clickedIndex, updatedItem.copy(isFavorite = !updatedItem.isFavorite))
		savedState.value = currentContent.copy(resultList = newList)
		updateUi()
		coroutineScope.runObservable(TOGGLE_FAVORITE_KEY) {
			return@runObservable favoritesRepository.toggleFavorite(updatedItem.webModel)
		}
	}
}