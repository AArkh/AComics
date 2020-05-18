package ru.anarkh.acomics.catalog.widget

import androidx.recyclerview.widget.DiffUtil
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.core.list.convenience.LoadingElement
import ru.anarkh.acomics.core.list.convenience.LoadingFailedElement

class CatalogItemsDiffsValidator : DiffUtil.ItemCallback<Any>() {

	override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
		return if (oldItem is CatalogComicsItem && newItem is CatalogComicsItem) {
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
		return if (oldItem is CatalogComicsItem && newItem is CatalogComicsItem) {
			return oldItem.isUIEqual(newItem)
		} else if (oldItem is CatalogSortConfig && newItem is CatalogSortConfig) {
			return oldItem.sorting == newItem.sorting
		} else if (oldItem is LoadingElement.Stub && newItem is LoadingElement.Stub) {
			return true
		} else if (oldItem is LoadingFailedElement.Stub && newItem is LoadingFailedElement.Stub) {
			return true
		} else false
	}

	private fun CatalogComicsItem.isUIEqual(other: CatalogComicsItem) : Boolean {
		// Некоторые поля меняются ну больно часто, поэтому сравниваем только важные элементы
		if (totalPages != other.totalPages) return false
		if (lastUpdated != other.lastUpdated) return false
		if (description != other.description) return false
		if (previewImage != other.previewImage) return false
		return true
	}
}