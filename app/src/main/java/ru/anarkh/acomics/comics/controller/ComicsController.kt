package ru.anarkh.acomics.comics.controller

import ru.anarkh.acomics.comics.model.*
import ru.anarkh.acomics.comics.repository.ComicsRepository
import ru.anarkh.acomics.comics.widget.ComicsWidget
import ru.anarkh.acomics.core.coroutines.ObservableScope
import ru.anarkh.acomics.core.coroutines.ObserverBuilder
import ru.anarkh.acomics.main.favorite.model.FavoritesRepository
import ru.arkharov.statemachine.SavedSerializable
import ru.arkharov.statemachine.StateRegistry

class ComicsController(
	private val catalogId: String,
	private val widget: ComicsWidget,
	private val repo: ComicsRepository,
	private val favoritesRepository: FavoritesRepository,
	private val coroutineScope: ObservableScope,
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
			return@runObservable repo.getComicsPage(catalogId)
		}
	}

	private fun initWidget() {
		widget.setOnPageChangeListener { pageIndex: Int ->
			val currentState = state.value as? Content ?: return@setOnPageChangeListener
			updateState(currentState.copy(currentPage = pageIndex))
			coroutineScope.runObservable("") {
				val model = favoritesRepository.getFavoriteById(catalogId) ?: return@runObservable
				if (pageIndex >= model.readPages) {
					favoritesRepository.update(model.copy(readPages = pageIndex.inc()))
				}
			}
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
	}

	private fun initAsyncObservers() {
		val observer = ObserverBuilder<ArrayList<ComicsPageModel>>(catalogId)
			.onFailed {
				updateState(Failed)
			}
			.onLoading {
				updateState(Loading)
			}
			.onSuccess { issues: List<ComicsPageModel> ->
				updateState(Content(issues, 0, false))
			}
			.build()
		coroutineScope.addObserver(observer)
	}

	private fun updateState(newState: ComicsWidgetState) {
		state.value = newState
		widget.updateState(newState)
	}
}