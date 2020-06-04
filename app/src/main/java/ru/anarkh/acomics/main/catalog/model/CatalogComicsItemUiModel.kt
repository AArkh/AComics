package ru.anarkh.acomics.main.catalog.model

import java.io.Serializable

data class CatalogComicsItemUiModel(
	private val webModel: CatalogComicsItemWebModel,
	val isFavorite: Boolean,
	val isDownloaded: DownloadingState
) : Serializable {
	val catalogId: String by lazy { webModel.catalogId }
	val title: String by lazy { webModel.title }
	val previewImage: String by lazy { webModel.previewImage }
	val description: String by lazy { webModel.description }
	val rating: String by lazy { webModel.rating }
	val lastUpdated: Long by lazy { webModel.lastUpdated }
	val totalPages: Int by lazy { webModel.totalPages }
	val totalSubscribers: Int by lazy { webModel.totalSubscribers }
}