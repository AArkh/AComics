package ru.anarkh.acomics.catalog.model

import java.io.Serializable

data class CatalogSortConfig(
	var sorting: CatalogSortingBy,
	val rating: LinkedHashSet<MPAARating>,
	var translationType: TranslationType
) : Serializable