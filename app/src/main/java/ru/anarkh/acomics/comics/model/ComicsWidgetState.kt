package ru.anarkh.acomics.comics.model

import java.io.Serializable

sealed class ComicsWidgetState : Serializable

object Initial : ComicsWidgetState()
object Loading : ComicsWidgetState()
object Failed : ComicsWidgetState()

data class Content(
	val issues: MutableList<ComicsPageUiModel>,
	val currentPage: Int,
	val isInFullscreen: Boolean,
	val bookmarkIndex: Int,
	val bookmarkType: Int
) : ComicsWidgetState()

data class ComicsPageUiModel(
	val comicsPageModel: ComicsPageModel,
	val state: State = State.Loading
) : Serializable {
	enum class State {
		Failed, Loading, Content
	}
}