package ru.anarkh.acomics.comics.widget.page

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import ru.anarkh.acomics.comics.model.ComicsPageModel
import ru.anarkh.acomics.comics.model.ComicsPageUiModel
import ru.anarkh.acomics.comics.model.Comment
import ru.anarkh.acomics.comics.model.UploaderData

class ComicsPageItemsDiffsValidator : DiffUtil.ItemCallback<Any>() {

	override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
		return if (oldItem is ComicsPageModel && newItem is ComicsPageModel) {
			return false
		} else if (oldItem is ComicsPageUiModel && newItem is ComicsPageUiModel) {
			return false
		} else if (oldItem is UploaderData && newItem is UploaderData) {
			return true
		} else if (oldItem is Comment && newItem is Comment) {
			return true
		} else false
	}

	@SuppressLint("DiffUtilEquals")
	override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
		return if (oldItem is ComicsPageModel && newItem is ComicsPageModel) {
			return oldItem.imageUrl == newItem.imageUrl
		} else if (oldItem is ComicsPageUiModel && newItem is ComicsPageUiModel) {
			return oldItem.state == newItem.state
		} else if (oldItem is UploaderData && newItem is UploaderData) {
			return oldItem.uploaderComment === newItem.uploaderComment
		} else if (oldItem is Comment && newItem is Comment) {
			return oldItem.body === newItem.body
		} else false
	}
}