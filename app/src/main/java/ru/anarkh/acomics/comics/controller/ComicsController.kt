package ru.anarkh.acomics.comics.controller

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.ComicsRouter
import ru.anarkh.acomics.comics.model.*
import ru.anarkh.acomics.comics.repository.ComicsRepository
import ru.anarkh.acomics.comics.widget.ComicsWidget
import ru.anarkh.acomics.core.BackButtonController
import ru.anarkh.acomics.core.coroutines.ObservableScope
import ru.anarkh.acomics.core.coroutines.ObserverBuilder
import ru.anarkh.acomics.core.error.ExceptionTelemetry
import ru.anarkh.acomics.core.state.SavedBoolean
import ru.anarkh.acomics.core.state.StateRegistry
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel
import ru.anarkh.acomics.main.favorite.model.FavoriteEntity
import ru.anarkh.acomics.main.favorite.model.FavoritesRepository
import kotlin.math.min

private const val UPDATE_BOOKMARK_KEY = "update_bookmark_key"

class ComicsController(
	private val comicsItemWebModel: CatalogComicsItemWebModel?,
	private val catalogId: String,
	private val widget: ComicsWidget,
	private val repo: ComicsRepository,
	private val favoritesRepository: FavoritesRepository,
	private val stateContainer: ComicsStateContainer,
	private val coroutineScope: ObservableScope,
	private val exceptionTelemetry: ExceptionTelemetry,
	private val context: Context,
	stateRegistry: StateRegistry,
	private val backButtonController: BackButtonController,
	private val router: ComicsRouter
) {

	private val wasAddedToFavorites = SavedBoolean("was_saved_to_favorites", false)

	init {
		stateRegistry.register(wasAddedToFavorites)
		initController()
		initAsyncObservers()
		initWidget()

		val currentState = stateContainer.state
		if (currentState is Initial) {
			loadComics()
		}
		widget.updateState(currentState)
	}

	private fun initController() {
		backButtonController.onBackListener = {
			if (wasAddedToFavorites.value) {
				router.finish(catalogId)
			} else {
				router.finish(null)
			}
			true
		}
	}

	private fun initWidget() {
		widget.setOnPageChangeListener { pageIndex: Int ->
			val currentState = stateContainer.state as? Content ?: return@setOnPageChangeListener
			updateState(currentState.copy(currentPage = pageIndex))
		}
		widget.onRetryClickListener {
			updateState(Loading)
			loadComics()
		}
		widget.onLeftButtonClickListener {
			val currentState = stateContainer.state as? Content ?: return@onLeftButtonClickListener
			if (currentState.currentPage <= 0) {
				return@onLeftButtonClickListener
			}
			updateState(currentState.copy(currentPage = currentState.currentPage.dec()))
			widget.scrollToPage(currentState.currentPage.dec())
		}
		widget.onRightButtonClickListener {
			val currentState = stateContainer.state as? Content ?: return@onRightButtonClickListener
			if (currentState.currentPage >= currentState.issues.lastIndex) {
				return@onRightButtonClickListener
			}
			updateState(currentState.copy(currentPage = currentState.currentPage.inc()))
			widget.scrollToPage(currentState.currentPage.inc())
		}
		widget.onSingleClickListener = {
			val currentState = stateContainer.state as? Content
			if (currentState != null) {
				updateState(currentState.copy(isInFullscreen = !currentState.isInFullscreen))
			}
		}
		widget.onImageLoadedListener = { page: Int ->
			updatePageToState(page, ComicsPageUiModel.State.Content)
		}
		widget.onImageLoadFailedListener = { page: Int ->
			updatePageToState(page, ComicsPageUiModel.State.Failed)
		}
		widget.onAddToBookmarksClickListener {
			val currentState = stateContainer.state as? Content ?: return@onAddToBookmarksClickListener
			coroutineScope.runObservable(UPDATE_BOOKMARK_KEY) {
				val model = favoritesRepository.getFavoriteById(catalogId)
				if (model == null) {
					if (comicsItemWebModel == null) {
						throw IllegalStateException("Failed to add new favorite from ComicsController")
					}
					val newFavorite = FavoriteEntity(
						catalogId = comicsItemWebModel.catalogId,
						previewImage = comicsItemWebModel.previewImage,
						totalPages = comicsItemWebModel.totalPages,
						readPages = currentState.currentPage,
						title = comicsItemWebModel.title,
						description = comicsItemWebModel.description
					)
					favoritesRepository.update(newFavorite)
					notifyNewFavoriteHasBeenAdded()
				} else {
					val isCurrentPage = model.readPages == currentState.currentPage
					if (isCurrentPage) {
						favoritesRepository.update(model.copy(readPages = -1))
						return@runObservable -1
					} else {
						favoritesRepository.update(model.copy(readPages = currentState.currentPage))
					}
				}
				return@runObservable currentState.currentPage
			}
		}
	}

	private fun initAsyncObservers() {
		val observer = ObserverBuilder<Content>(catalogId)
			.onFailed {
				exceptionTelemetry.recordException(it)
				updateState(Failed)
			}
			.onLoading {
				updateState(Loading)
			}
			.onSuccess { content: Content ->
				updateState(content)
				widget.scrollToPage(content.currentPage)
			}
			.build()
		coroutineScope.addObserver(observer)
		val updateBookmarkObserver = ObserverBuilder<Int>(UPDATE_BOOKMARK_KEY)
			.onFailed {
				exceptionTelemetry.recordException(it)
			}
			.onSuccess { bookmarkedPageIndex: Int ->
				val currentState = stateContainer.state as? Content ?: return@onSuccess
				updateState(currentState.copy(bookmarkIndex = bookmarkedPageIndex))
			}
			.build()
		coroutineScope.addObserver(updateBookmarkObserver)
	}

	private fun loadComics() {
		coroutineScope.runObservable(catalogId) {
			val bookmarkIndex = favoritesRepository.getFavoriteById(catalogId)?.readPages ?: -1
			val pages = repo.getComicsPages(catalogId)
				.mapTo(ArrayList()) { comicsPageModel: ComicsPageModel ->
					return@mapTo ComicsPageUiModel(comicsPageModel)
				}
			var currentPage = 0
			if (pages.isNotEmpty() && bookmarkIndex >= 0) {
				currentPage = min(pages.lastIndex, bookmarkIndex)
			}
			return@runObservable Content(pages, currentPage, false, bookmarkIndex)
		}
	}

	private fun updateState(newState: ComicsWidgetState) {
		stateContainer.state = newState
		widget.updateState(newState)
	}

	private fun updatePageToState(page: Int, pageState: ComicsPageUiModel.State) {
		val currentState = stateContainer.state as? Content
		if (currentState != null) {
			val newList = currentState.issues.mapTo(ArrayList(currentState.issues.size)) {
				return@mapTo if (it.comicsPageModel.page == page) {
					it.copy(state = pageState)
				} else it
			}
			updateState(currentState.copy(issues = newList))
		}
	}

	private suspend fun notifyNewFavoriteHasBeenAdded() {
		withContext(Dispatchers.Main) {
			wasAddedToFavorites.value = true
			Toast.makeText(
				context,
				R.string.comics_new_favorite_added_message,
				Toast.LENGTH_SHORT
			).show()
		}
	}
}