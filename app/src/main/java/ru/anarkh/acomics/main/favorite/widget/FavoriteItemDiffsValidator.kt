package ru.anarkh.acomics.main.favorite.widget

import androidx.recyclerview.widget.DiffUtil
import ru.anarkh.acomics.core.db.FavoriteEntity
import ru.anarkh.acomics.core.list.convenience.LoadingElement
import ru.anarkh.acomics.core.list.convenience.LoadingFailedElement

class FavoriteItemDiffsValidator : DiffUtil.ItemCallback<Any>() {

	override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
		return if (oldItem is FavoriteEntity && newItem is FavoriteEntity) {
			return oldItem.catalogId == newItem.catalogId
		} else if (oldItem is LoadingElement.Stub && newItem is LoadingElement.Stub) {
			return true
		} else if (oldItem is LoadingFailedElement.Stub && newItem is LoadingFailedElement.Stub) {
			return true
		} else false
	}

	override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
		return if (oldItem is FavoriteEntity && newItem is FavoriteEntity) {
			return oldItem.isUIEqual(newItem)
		} else if (oldItem is LoadingElement.Stub && newItem is LoadingElement.Stub) {
			return true
		} else if (oldItem is LoadingFailedElement.Stub && newItem is LoadingFailedElement.Stub) {
			return true
		} else false
	}

	private fun FavoriteEntity.isUIEqual(other: FavoriteEntity): Boolean {
		// Некоторые поля меняются ну больно часто, поэтому сравниваем только важные элементы
		if (totalPages != other.totalPages) return false
		if (title != other.title) return false
		if (description != other.description) return false
		if (previewImage != other.previewImage) return false
		if (readPages != other.readPages) return false
		return true
	}
}