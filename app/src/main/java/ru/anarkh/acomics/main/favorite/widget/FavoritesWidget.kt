package ru.anarkh.acomics.main.favorite.widget

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.core.list.ListConfig
import ru.anarkh.acomics.core.list.MultipleVHListAdapter
import ru.anarkh.acomics.main.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.main.catalog.widget.CatalogLoadingWidget
import ru.anarkh.acomics.main.favorite.model.FavoriteEntity

class FavoritesWidget(
	private val noFavoritesWidget: CatalogLoadingWidget,
	private val recyclerView: RecyclerView,
	quantityStringParser: FixedLocaleQuantityStringParser
) {

	private val favoriteListElement = FavoriteListElement(quantityStringParser)
	private val adapter: MultipleVHListAdapter

	init {
		val listConfig = ListConfig(
			Triple(
				favoriteListElement,
				FavoriteViewHolder::class.java,
				FavoriteEntity::class.java
			)
		)
		adapter = MultipleVHListAdapter(
			FavoriteItemDiffsValidator(),
			listConfig
		)
		val layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false)
		recyclerView.adapter = adapter
		recyclerView.layoutManager = layoutManager
	}

	fun updateState(state: FavoritesState) {
		when (state) {
			Failed -> {
				noFavoritesWidget.showFailed()
				recyclerView.visibility = View.GONE
			}
			Initial -> {
				noFavoritesWidget.showLoading()
				recyclerView.visibility = View.GONE
			}
			Loading -> {
				noFavoritesWidget.showLoading()
				recyclerView.visibility = View.GONE
			}
			NoSavedFavorites -> {
				noFavoritesWidget.showNoData()
				recyclerView.visibility = View.GONE
			}
			is Content -> {
				noFavoritesWidget.hide()
				recyclerView.visibility = View.VISIBLE
				adapter.submitList(state.favorites)
			}
		}
	}

	fun onFavoriteItemClick(listener: (catalogId: String, totalPages: Int) -> Unit) {
		favoriteListElement.onItemClickListener = listener
	}

	fun onRemoveFromFavoritesClick(listener: (catalogId: String) -> Unit) {
		favoriteListElement.onFavoriteClickListener = listener
	}
}