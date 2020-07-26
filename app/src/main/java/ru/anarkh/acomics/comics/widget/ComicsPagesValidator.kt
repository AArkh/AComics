package ru.anarkh.acomics.comics.widget

import androidx.recyclerview.widget.DiffUtil
import ru.anarkh.acomics.comics.model.ComicsPageUiModel

class ComicsPagesValidator : DiffUtil.ItemCallback<Any>() {

	override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
		if (oldItem is ComicsPageUiModel && newItem is ComicsPageUiModel) {
			return oldItem.comicsPageModel.page == newItem.comicsPageModel.page
		}
		return false
	}

	override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
		if (oldItem is ComicsPageUiModel && newItem is ComicsPageUiModel) {
			return oldItem.state == newItem.state
		}
		return false
	}
}