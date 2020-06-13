package ru.anarkh.acomics.comics.controller

import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.anarkh.acomics.comics.model.*
import ru.anarkh.acomics.comics.repository.ComicsRepository
import ru.anarkh.acomics.comics.widget.ComicsWidget
import ru.anarkh.acomics.core.coroutines.ObservableScope
import ru.anarkh.acomics.core.coroutines.ObserverBuilder
import ru.anarkh.acomics.main.favorite.model.FavoritesRepository
import ru.arkharov.statemachine.SavedSerializable
import ru.arkharov.statemachine.StateRegistry
import java.net.ConnectException
import kotlin.math.min

class ComicsController(
	private val catalogId: String,
	private val widget: ComicsWidget,
	private val repo: ComicsRepository,
	private val favoritesRepository: FavoritesRepository,
	private val coroutineScope: ObservableScope,
	private val crashlytics: FirebaseCrashlytics,
	stateRegistry: StateRegistry
) {

	private val state = SavedSerializable<ComicsWidgetState>(catalogId, Initial)

	init {
		stateRegistry.register(state)
		initAsyncObservers()
		initWidget()

		val currentState = state.value ?: Initial
		if (state.value is Initial) {
			loadComics()
		}
		widget.updateState(currentState)
	}

	private fun loadComics() {
		coroutineScope.runObservable(catalogId) {
			var bookmarkIndex = favoritesRepository.getFavoriteById(catalogId)
				?.readPages
				?.dec()
				?: 0
			val pages = repo.getComicsPages(catalogId)
			if (pages.isNotEmpty()) {
				bookmarkIndex = min(pages.lastIndex, bookmarkIndex)
			}
			return@runObservable Content(pages, bookmarkIndex, false)
		}
	}

	private fun initWidget() {
		widget.setOnPageChangeListener { pageIndex: Int ->
			val currentState = state.value as? Content ?: return@setOnPageChangeListener
			updateState(currentState.copy(currentPage = pageIndex))
		}
		widget.onRetryClickListener {
			updateState(Loading)
			loadComics()
		}
		widget.onLeftButtonClickListener {
			val currentState = state.value as? Content ?: return@onLeftButtonClickListener
			updateState(currentState.copy(currentPage = currentState.currentPage.dec()))
		}
		widget.onRightButtonClickListener {
			val currentState = state.value as? Content ?: return@onRightButtonClickListener
			updateState(currentState.copy(currentPage = currentState.currentPage.inc()))
		}
		widget.onSingleClickListener = {
			val currentState = state.value as? Content
			if (currentState != null) {
				updateState(currentState.copy(isInFullscreen = !currentState.isInFullscreen))
			}
		}
		widget.onAddToBookmarksClickListener {
			val currentState = state.value as? Content ?: return@onAddToBookmarksClickListener
			coroutineScope.runObservable {
				val model = favoritesRepository.getFavoriteById(catalogId) ?: return@runObservable
				favoritesRepository.update(model.copy(readPages = currentState.currentPage.inc()))
			}
		}
	}

	private fun initAsyncObservers() {
		val observer = ObserverBuilder<Content>(catalogId)
			.onFailed {
				if (it !is ConnectException) {
					crashlytics.recordException(it)
				}
				updateState(Failed)
			}
			.onLoading {
				updateState(Loading)
			}
			.onSuccess { content: Content ->
				updateState(content)
			}
			.build()
		coroutineScope.addObserver(observer)
	}

	private fun updateState(newState: ComicsWidgetState) {
		state.value = newState
		widget.updateState(newState)
	}
}