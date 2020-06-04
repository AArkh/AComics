package ru.anarkh.acomics.comics.controller

import ru.anarkh.acomics.comics.model.*
import ru.anarkh.acomics.comics.repository.ComicsRepository
import ru.anarkh.acomics.comics.widget.ComicsWidget
import ru.anarkh.acomics.core.coroutines.CoroutineScope
import ru.anarkh.acomics.core.coroutines.ObserverBuilder
import ru.arkharov.statemachine.SavedSerializable
import ru.arkharov.statemachine.StateRegistry

class ComicsController(
	private val catalogId: String,
	private val widget: ComicsWidget,
	private val repo: ComicsRepository,
	private val coroutineScope: CoroutineScope,
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
		coroutineScope.runCoroutine(catalogId) {
			return@runCoroutine repo.getComicsPage(catalogId)
		}
	}

	private fun initWidget() {
		widget.setOnPageChangeListener { pageIndex: Int ->
			val currentState = state.value as? Content ?: return@setOnPageChangeListener
			updateState(Content(currentState.issues, pageIndex))
		}
		widget.onRetryClickListener {
			updateState(Loading)
			loadComics()
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
				updateState(Content(issues, 0))
			}
			.build()
		coroutineScope.addObserver(observer)
	}

	private fun updateState(newState: ComicsWidgetState) {
		state.value = newState
		widget.updateState(newState)
	}
}