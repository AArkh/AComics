package ru.anarkh.acomics.catalog.widget.paging

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

object Loading : PagingState()

object Failed: PagingState()

object Initial : PagingState()

object NoData: PagingState()