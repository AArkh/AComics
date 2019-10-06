package ru.anarkh.acomics.catalog.model

import java.io.Serializable

//todo Добавить всякие фильтры сюдой
data class CatalogSortConfig(
	var sorting: CatalogSortingBy = CatalogSortingBy.BY_DATE
) : Serializable