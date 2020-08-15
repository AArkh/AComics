package ru.anarkh.acomics.main.catalog.controller

import ru.anarkh.acomics.core.BackButtonController
import ru.anarkh.acomics.core.coroutines.ObservableScope
import ru.anarkh.acomics.core.coroutines.ObserverBuilder
import ru.anarkh.acomics.core.error.ExceptionTelemetry
import ru.anarkh.acomics.core.keyboard.EditTextKeyboardWidget
import ru.anarkh.acomics.core.state.SavedSerializable
import ru.anarkh.acomics.core.state.StateRegistry
import ru.anarkh.acomics.main.catalog.CatalogRouter
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemUiModel
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel
import ru.anarkh.acomics.main.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.main.catalog.model.CatalogSortingBy
import ru.anarkh.acomics.main.catalog.repository.CatalogRepository
import ru.anarkh.acomics.main.catalog.repository.CatalogSortConfigRepository
import ru.anarkh.acomics.main.catalog.widget.CatalogSearchWidget
import ru.anarkh.acomics.main.catalog.widget.CatalogWidget
import ru.anarkh.acomics.main.catalog.widget.filter.CatalogFilterDialogWidget
import ru.anarkh.acomics.main.catalog.widget.filter.CatalogSortDialogWidget
import ru.anarkh.acomics.main.catalog.widget.paging.*
import ru.anarkh.acomics.main.favorite.controller.REMOVE_FROM_FAVORITES_KEY
import ru.anarkh.acomics.main.favorite.model.FavoritesRepository
import java.io.Serializable

const val TOGGLE_FAVORITE_KEY = "toggle_favorite_key"

private const val BUNDLE_KEY = "catalog_controller_bundle_key"
private const val INITIAL_TASK_KEY = "catalog_controller_initial"
private const val PAGINATION_TASK_KEY = "catalog_controller_pagination"
private const val SEARCH_TASK_KEY = "catalog_controller_search"

class CatalogController(
	private val router: CatalogRouter,
	private val widget: CatalogWidget,
	private val searchWidget: CatalogSearchWidget,
	private val sortDialogWidget: CatalogSortDialogWidget,
	private val filterDialogWidget: CatalogFilterDialogWidget,
	private val editTextKeyboardWidget: EditTextKeyboardWidget,
	private val sortConfigRepository: CatalogSortConfigRepository,
	private val catalogRepository: CatalogRepository,
	private val favoritesRepository: FavoritesRepository,
	private val coroutineScope: ObservableScope,
	private val exceptionTelemetry: ExceptionTelemetry,
	private val backButtonController: BackButtonController,
	stateRegistry: StateRegistry
) {

	private val savedState = SavedSerializable<PagingState>(BUNDLE_KEY, Initial)

	init {
		stateRegistry.register(savedState)
		initController()
		initWidget()
		initDialogs()
		initAsyncObservers()
		updateUi()
	}

	private fun initController() {
		backButtonController.onBackListener = {
			if (widget.firstVisiblePosition() > 10) {
				widget.scrollToStart()
				true
			} else false
		}
		router.setOnComicsScreenReturnCallback { catalogId: String ->
			toggleFavorite(catalogId)
			coroutineScope.runObservable(TOGGLE_FAVORITE_KEY) {
				return@runObservable favoritesRepository.getFavoriteById(catalogId)
					?: throw IllegalStateException("favorite was null")
			}
		}
	}

	private fun initWidget() {
		widget.onComicsClick { model: CatalogComicsItemWebModel ->
			editTextKeyboardWidget.hide()
			router.openComicsPage(model)
		}
		widget.onAddToFavoritesClick { model: CatalogComicsItemUiModel ->
			toggleFavorite(model.catalogId)
			coroutineScope.runObservable(TOGGLE_FAVORITE_KEY) {
				return@runObservable favoritesRepository.toggleFavorite(model.webModel)
			}
		}
		widget.onReportComicsClick { model: CatalogComicsItemUiModel ->
			router.openReport(model.title)
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
			} else if (currentState is SearchContent) {
				savedState.value =
					currentState.copy(state = SearchContent.SearchContentState.LOADING)
				updateUi()
				loadSearchList(currentState)
			}
		}
		searchWidget.searchInputCallback = { userInput: String ->
			val previousState = savedState.value as? SearchContent
			if (userInput != previousState?.searchInput && userInput.isNotBlank()) {
				val newState = SearchContent(
					userInput,
					previousState?.searchResultList ?: emptyList(),
					SearchContent.SearchContentState.LOADING
				)
				savedState.value = newState
				updateUi()
				loadSearchList(newState)
			}
		}
		searchWidget.openSearchCallback = {
			if (savedState.value !is SearchContent) {
				editTextKeyboardWidget.show()
				savedState.value = SearchContent(
					"", emptyList(), SearchContent.SearchContentState.INITIAL
				)
				updateUi()
			}
		}
		searchWidget.closeSearchCallback = {
			if (savedState.value is SearchContent) {
				editTextKeyboardWidget.hide()
				//fixme тут кэш сделать и лезть туда, чтобы красивый переход был.
				savedState.value = Initial
				updateUi()
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
				exceptionTelemetry.recordException(it)
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
				exceptionTelemetry.recordException(it)
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
			.onFailed { exceptionTelemetry.recordException(it) }
			.onSuccess { catalogId: String ->
				val currentState = savedState.value
				val currentList: List<Serializable> = when (currentState) {
					is Content -> currentState.resultList
					is SearchContent -> currentState.searchResultList
					else -> null
				} ?: return@onSuccess
				val newList = currentList.map {
					return@map if (it is CatalogComicsItemUiModel && it.catalogId == catalogId) {
						return@map it.copy(isFavorite = false)
					} else it
				}
				when (currentState) {
					is Content -> savedState.value = currentState.copy(resultList = newList)
					is SearchContent -> {
						savedState.value = currentState.copy(searchResultList = newList)
					}
				}
				updateUi()
			}
			.build()
		coroutineScope.addObserver(removeFavoriteObserver)
		val searchObserver = ObserverBuilder<Pair<String, List<Serializable>>>(SEARCH_TASK_KEY)
			.onFailed {
				exceptionTelemetry.recordException(it)
				val currentState: SearchContent = savedState.value as? SearchContent
					?: return@onFailed
				savedState.value = currentState.copy(
					state = SearchContent.SearchContentState.FAILED
				)
				updateUi()
			}
			.onSuccess { pair: Pair<String, List<Serializable>> ->
				val currentState: SearchContent = savedState.value as? SearchContent
					?: return@onSuccess
				val searchedInput = pair.first
				val resultList = pair.second
				if (currentState.searchInput != searchedInput) {
					return@onSuccess
				}
				savedState.value = currentState.copy(
					searchResultList = resultList,
					state = SearchContent.SearchContentState.CONTENT
				)
				updateUi()
			}
			.build()
		coroutineScope.addObserver(searchObserver)
	}

	private fun updateUi() {
		val currentState = savedState.value ?: Initial
		widget.updateState(currentState)
		if (currentState is SearchContent) {
			searchWidget.updateState(currentState)
		}
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

	private fun loadSearchList(currentState: SearchContent) {
		coroutineScope.runObservable(SEARCH_TASK_KEY) {
			return@runObservable Pair<String, List<Serializable>>(
				currentState.searchInput,
				catalogRepository.search(currentState.searchInput)
			)
		}
	}

	private fun toggleFavorite(catalogId: String) {
		val currentContent = savedState.value
		val list: List<Serializable> = when (currentContent) {
			is Content -> currentContent.resultList
			is SearchContent -> currentContent.searchResultList
			else -> null
		} ?: return
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
		val updatedItem = list[clickedIndex] as? CatalogComicsItemUiModel ?: return
		newList.removeAt(clickedIndex)
		newList.add(clickedIndex, updatedItem.copy(isFavorite = !updatedItem.isFavorite))
		when (currentContent) {
			is Content -> {
				savedState.value = currentContent.copy(resultList = newList)
			}
			is SearchContent -> {
				savedState.value = currentContent.copy(searchResultList = newList)
			}
		}
		updateUi()
	}
}