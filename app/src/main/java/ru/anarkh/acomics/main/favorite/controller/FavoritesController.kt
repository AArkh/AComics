package ru.anarkh.acomics.main.favorite.controller

import ru.anarkh.acomics.core.coroutines.ObservableScope
import ru.anarkh.acomics.core.coroutines.ObserverBuilder
import ru.anarkh.acomics.main.catalog.CatalogRouter
import ru.anarkh.acomics.main.catalog.controller.TOGGLE_FAVORITE_KEY
import ru.anarkh.acomics.main.favorite.model.FavoriteEntity
import ru.anarkh.acomics.main.favorite.model.FavoritesRepository
import ru.anarkh.acomics.main.favorite.widget.*
import ru.arkharov.statemachine.SavedSerializable
import ru.arkharov.statemachine.StateRegistry

const val REMOVE_FROM_FAVORITES_KEY = "remove_from_favorites_key"

private const val INITIAL_TASK_KEY = "favorites_initial_task_key"

class FavoritesController(
	private val router: CatalogRouter,
	private val repository: FavoritesRepository,
	private val widget: FavoritesWidget,
	private val scope: ObservableScope,
	stateRegistry: StateRegistry
) {

	private val state = SavedSerializable<FavoritesState>(this::class.java.name, Initial)

	init {
		stateRegistry.register(state)
		initWidget()
		initObservers()
		if (state.value == Initial) {
			scope.runObservable(INITIAL_TASK_KEY) {
				return@runObservable repository.getFavorites()
			}
		}
		updateUi()
	}

	private fun initWidget() {
		widget.onFavoriteItemClick { catalogId: String, totalPages: Int ->
			router.openComicsPage(catalogId, totalPages)
		}
		widget.onRemoveFromFavoritesClick { catalogId: String ->
			scope.runObservable(REMOVE_FROM_FAVORITES_KEY) {
				repository.delete(catalogId)
				val currentState = state.value as? Content ?: return@runObservable catalogId
				val newList = currentState.favorites.filter { favorite ->
					return@filter favorite.catalogId != catalogId
				}
				if (newList.isEmpty()) {
					state.value = NoSavedFavorites
				} else {
					state.value = Content(newList)
				}
				return@runObservable catalogId
			}
		}
	}

	private fun initObservers() {
		val observer = ObserverBuilder<List<FavoriteEntity>>(INITIAL_TASK_KEY)
			.onFailed {
				//todo log exception here
				state.value = Failed
				updateUi()
			}
			.onLoading {
				state.value = Loading
				updateUi()
			}
			.onSuccess { favorites: List<FavoriteEntity> ->
				if (favorites.isEmpty()) {
					state.value = NoSavedFavorites
					updateUi()
					return@onSuccess
				}
				val newState = Content(favorites)
				state.value = newState
				updateUi()
			}
			.build()
		scope.addObserver(observer)
		val favoriteObserver = ObserverBuilder<FavoriteEntity>(TOGGLE_FAVORITE_KEY)
			.onSuccess { favoriteEntity: FavoriteEntity ->
				val currentState: FavoritesState = state.value ?: return@onSuccess
				val newState: FavoritesState = if (currentState is Content) {
					val currentList = currentState.favorites
					if (currentList.contains(favoriteEntity)) {
						Content(currentList.filter { it.catalogId != favoriteEntity.catalogId })
					} else {
						val newList = currentList.toMutableList()
						newList.add(favoriteEntity)
						Content(newList)
					}
				} else {
					Content(arrayListOf(favoriteEntity))
				}
				state.value = newState
				updateUi()
			}
			.build()
		scope.addObserver(favoriteObserver)
		val removeFavoriteObserver = ObserverBuilder<String>(REMOVE_FROM_FAVORITES_KEY)
			.onSuccess {
				updateUi()
			}
			.build()
		scope.addObserver(removeFavoriteObserver)
	}

	private fun updateUi() {
		val currentValue = state.value ?: Initial
		widget.updateState(currentValue)
	}
}