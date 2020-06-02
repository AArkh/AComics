package ru.anarkh.acomics.catalog.model

import java.io.Serializable

data class CatalogSortConfig(
	var sorting: CatalogSortingBy,
	val rating: LinkedHashSet<MPAARating>,
	var translationType: TranslationType
) : Serializable {

	override fun equals(other: Any?): Boolean {
		if (other !is CatalogSortConfig) return false
		if (sorting != other.sorting) return false
		if (translationType != other.translationType) return false
		return rating.size == other.rating.size
			&& rating.containsAll(other.rating)
			&& other.rating.containsAll(rating)
	}

	override fun hashCode(): Int {
		var result = sorting.hashCode()
		result = 31 * result + rating.hashCode()
		result = 31 * result + translationType.hashCode()
		return result
	}

	fun copy() : CatalogSortConfig {
		return CatalogSortConfig(
			sorting,
			LinkedHashSet(rating),
			translationType
		)
	}
}