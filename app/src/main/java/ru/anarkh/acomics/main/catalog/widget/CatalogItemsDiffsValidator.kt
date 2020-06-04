package ru.anarkh.acomics.main.catalog.widget

import androidx.recyclerview.widget.DiffUtil
import ru.anarkh.acomics.core.list.convenience.LoadingElement
import ru.anarkh.acomics.core.list.convenience.LoadingFailedElement
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemUiModel
import ru.anarkh.acomics.main.catalog.model.CatalogSortConfig

class CatalogItemsDiffsValidator : DiffUtil.ItemCallback<Any>() {

	override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
		return if (oldItem is CatalogComicsItemUiModel && newItem is CatalogComicsItemUiModel) {
			return oldItem.title == newItem.title
		} else if (oldItem is CatalogSortConfig && newItem is CatalogSortConfig) {
			return true
		} else if (oldItem is LoadingElement.Stub && newItem is LoadingElement.Stub) {
			return true
		} else if (oldItem is LoadingFailedElement.Stub && newItem is LoadingFailedElement.Stub) {
			return true
		} else false
	}

	override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
		return if (oldItem is CatalogComicsItemUiModel && newItem is CatalogComicsItemUiModel) {
			return oldItem.isUIEqual(newItem)
		} else if (oldItem is CatalogSortConfig && newItem is CatalogSortConfig) {
			return oldItem.sorting == newItem.sorting
		} else if (oldItem is LoadingElement.Stub && newItem is LoadingElement.Stub) {
			return true
		} else if (oldItem is LoadingFailedElement.Stub && newItem is LoadingFailedElement.Stub) {
			return true
		} else false
	}

	private fun CatalogComicsItemUiModel.isUIEqual(other: CatalogComicsItemUiModel) : Boolean {
		// Некоторые поля меняются ну больно часто, поэтому сравниваем только важные элементы
		if (isFavorite != other.isFavorite) return false
		if (totalPages != other.totalPages) return false
		if (lastUpdated != other.lastUpdated) return false
		if (description != other.description) return false
		if (previewImage != other.previewImage) return false
		return true
	}
}