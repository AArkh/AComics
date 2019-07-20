package ru.anarkh.acomics.catalog.controller

import androidx.recyclerview.widget.DiffUtil
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.model.CatalogSortModel

class CatalogItemsDiffsValidator : DiffUtil.ItemCallback<Any>() {

	override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
		return if (oldItem is CatalogComicsItem && newItem is CatalogComicsItem) {
			return oldItem.hyperLink == newItem.hyperLink
		} else if (oldItem is CatalogSortModel && newItem is CatalogSortModel) {
			return true
		} else false
	}

	override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
		return if (oldItem is CatalogComicsItem && newItem is CatalogComicsItem) {
			return oldItem.isUIEqual(newItem)
		} else if (oldItem is CatalogSortModel && newItem is CatalogSortModel) {
			return oldItem.x == newItem.x
		} else false
	}

	private fun CatalogComicsItem.isUIEqual(other: CatalogComicsItem) : Boolean {
		// Некоторые поля меняются ну больно часто, поэтому сравниваем только важные элементы
		if (formattedTotalPages != other.formattedTotalPages) return false
		if (lastUpdated != other.lastUpdated) return false
		if (title != other.title) return false
		if (description != other.description) return false
		if (previewImage != other.previewImage) return false
		if (hyperLink != other.hyperLink) return false
		return true
	}
}