package ru.anarkh.acomics.main.catalog.widget.paging

import java.io.Serializable

sealed class PagingState : Serializable

data class Content(
	val resultList: List<Serializable>,
	val page: Int,
	val state: ContentState
) : PagingState(), Serializable {
	enum class ContentState {
		HAS_MORE, LOADING_NEXT_PAGE, FAILED, END_REACHED
	}
}

data class SearchContent(
	val searchInput: String,
	val searchResultList: List<Serializable>,
	val state: SearchContentState
) : PagingState(), Serializable {
	enum class SearchContentState {
		INITIAL, LOADING, FAILED, CONTENT
	}
}

object Loading : PagingState()

object Failed : PagingState()

object Initial : PagingState()

object NoData : PagingState()