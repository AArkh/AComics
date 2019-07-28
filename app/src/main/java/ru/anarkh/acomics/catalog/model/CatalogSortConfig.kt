package ru.anarkh.acomics.catalog.model

import java.io.Serializable

data class CatalogSortConfig(
	var sorting: CatalogSortingBy = CatalogSortingBy.BY_DATE
) : Serializable