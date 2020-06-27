package ru.anarkh.acomics.main.catalog.widget

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.core.list.ListConfig
import ru.anarkh.acomics.core.list.MultipleVHListAdapter
import ru.anarkh.acomics.core.list.convenience.LoadingElement
import ru.anarkh.acomics.core.list.convenience.LoadingFailedElement
import ru.anarkh.acomics.core.list.convenience.LoadingFailedViewHolder
import ru.anarkh.acomics.core.list.convenience.LoadingViewHolder
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemUiModel
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel
import ru.anarkh.acomics.main.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.main.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.main.catalog.widget.filter.CatalogSortItemViewHolder
import ru.anarkh.acomics.main.catalog.widget.filter.CatalogSortListElement
import ru.anarkh.acomics.main.catalog.widget.paging.*

class CatalogWidget(
	private val noDataWidget: CatalogLoadingWidget,
	private val recyclerView: RecyclerView,
	quantityStringParser: FixedLocaleQuantityStringParser
) {

	var onLoadingElementReached: (() -> Unit)? = null

	private val comicsListElement = CatalogComicsListElement(quantityStringParser)
	private val sortListElement = CatalogSortListElement()
	private val loadingFailedElement = LoadingFailedElement()
	private val adapter: MultipleVHListAdapter
	private val layoutManager: LinearLayoutManager

	init {
		val listConfig = ListConfig(
			Triple(
				comicsListElement,
				CatalogViewHolder::class.java,
				CatalogComicsItemUiModel::class.java
			),
			Triple(
				sortListElement,
				CatalogSortItemViewHolder::class.java,
				CatalogSortConfig::class.java
			),
			Triple(
				LoadingElement(),
				LoadingViewHolder::class.java,
				LoadingElement.Stub::class.java
			),
			Triple(
				loadingFailedElement,
				LoadingFailedViewHolder::class.java,
				LoadingFailedElement.Stub::class.java
			)
		)
		adapter = MultipleVHListAdapter(
			CatalogItemsDiffsValidator(),
			listConfig
		)
		layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false)
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val lastVisible = layoutManager.findLastVisibleItemPosition()
				val holder = recyclerView.findViewHolderForLayoutPosition(lastVisible)
				if (holder is LoadingViewHolder) {
					onLoadingElementReached?.invoke()
				}
			}
		})
		recyclerView.layoutManager = layoutManager
		recyclerView.adapter = adapter
	}

	fun updateState(state: PagingState) {
		when (state) {
			is Initial -> {
				noDataWidget.showLoading()
				hideList()
			}
			is Loading -> {
				noDataWidget.showLoading()
				hideList()
			}
			is NoData -> {
				noDataWidget.showNoData()
				hideList()
			}
			is Failed -> {
				noDataWidget.showFailed()
				hideList()
			}
			is Content -> {
				noDataWidget.hide()
				recyclerView.visibility = View.VISIBLE
				val list = state.resultList.clone()
				when (state.state) {
					Content.ContentState.HAS_MORE -> list.add(LoadingElement.Stub)
					Content.ContentState.LOADING_NEXT_PAGE -> list.add(LoadingElement.Stub)
					Content.ContentState.FAILED -> list.add(LoadingFailedElement.Stub)
					Content.ContentState.END_REACHED -> {
					} // footer прикрутить какой-нибудь.
				}
				adapter.submitList(list)
			}
			is SearchContent -> {
				when (state.state) {
					SearchContent.SearchContentState.CONTENT -> {
						if (state.searchResultList.isEmpty()) {
							noDataWidget.showNoData()
							hideList()
						} else {
							noDataWidget.hide()
							recyclerView.visibility = View.VISIBLE
							adapter.submitList(state.searchResultList)
						}
					}
					SearchContent.SearchContentState.LOADING -> {
						noDataWidget.hide()
						recyclerView.visibility = View.VISIBLE
						adapter.submitList(listOf(LoadingElement.Stub))
					}
					SearchContent.SearchContentState.FAILED -> {
						noDataWidget.hide()
						recyclerView.visibility = View.VISIBLE
						adapter.submitList(listOf(LoadingFailedElement.Stub))
					}
					SearchContent.SearchContentState.INITIAL -> {
						hideList()
						noDataWidget.showNoData()
					}
				}
			}
		}
	}

	fun firstVisiblePosition(): Int = layoutManager.findFirstVisibleItemPosition()

	fun scrollToStart() {
		recyclerView.smoothScrollToPosition(0)
	}

	fun onComicsClick(listener: (webModel: CatalogComicsItemWebModel) -> Unit) {
		comicsListElement.onItemClickListener = listener
	}

	fun onAddToFavoritesClick(listener: (catalogId: String) -> Unit) {
		comicsListElement.onFavoriteClickListener = listener
	}

	fun onSortIconClick(listener: (() -> Unit)) {
		sortListElement.onSortIconClickListener = listener
	}

	fun onFilterItemClick(listener: (() -> Unit)) {
		sortListElement.onFilterItemClickListener = listener
	}

	fun onRetryButtonClick(listener: () -> Unit) {
		noDataWidget.retryButtonClickListener = listener
	}

	fun onRetryLoadNextPageButtonClick(listener: () -> Unit) {
		loadingFailedElement.clickListener = listener
	}

	private fun hideList() {
		adapter.submitList(emptyList())
		recyclerView.visibility = View.GONE
	}

	private fun List<Any>.clone(): MutableList<Any> {
		return mapTo(ArrayList(size.inc()), { return@mapTo it })
	}
}