package ru.anarkh.acomics.comics.model

import java.io.Serializable

sealed class ComicsWidgetState : Serializable

object Initial: ComicsWidgetState()
object Loading : ComicsWidgetState()
object Failed : ComicsWidgetState()

data class Content(
	val issues: List<ComicsPage>,
	val currentPage: Int
) : ComicsWidgetState()