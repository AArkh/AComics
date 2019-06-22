package ru.anarkh.acomics.catalog.controller

import androidx.recyclerview.widget.DiffUtil
import ru.anarkh.acomics.catalog.model.CatalogComicsItem

class CatalogItemsDiffsValidator : DiffUtil.ItemCallback<CatalogComicsItem>() {

	override fun areItemsTheSame(oldItem: CatalogComicsItem, newItem: CatalogComicsItem): Boolean {
		return oldItem.hyperLink == newItem.hyperLink
	}

	override fun areContentsTheSame(oldItem: CatalogComicsItem, newItem: CatalogComicsItem): Boolean {
		return oldItem.isUIEqual(newItem)
	}

	private fun CatalogComicsItem.isUIEqual(other: CatalogComicsItem) : Boolean {
		// Некоторые поля меняются ну больно часто, поэтому сравниваем только важные элементы
		if (totalPages != other.totalPages) return false
		if (lastUpdated != other.lastUpdated) return false
		if (title != other.title) return false
		if (description != other.description) return false
		if (previewImage != other.previewImage) return false
		if (hyperLink != other.hyperLink) return false
		return true
	}
}