package ru.anarkh.acomics.main.catalog.model

import androidx.annotation.IntRange
import java.io.Serializable

data class CatalogSortConfig(
	var sorting: CatalogSortingBy,
	val rating: LinkedHashSet<MPAARating>,
	var translationType: TranslationType,
	@IntRange(from = 0L) var minPages: Int
) : Serializable {

	override fun equals(other: Any?): Boolean {
		if (other !is CatalogSortConfig) return false
		if (sorting != other.sorting) return false
		if (translationType != other.translationType) return false
		if (minPages != other.minPages) return false
		return rating.size == other.rating.size && rating.containsAll(other.rating)
	}

	override fun hashCode(): Int {
		var result = sorting.hashCode()
		result = 31 * result + rating.hashCode()
		result = 31 * result + translationType.hashCode()
		result = 31 * result + minPages
		return result
	}

	fun copy(): CatalogSortConfig {
		return CatalogSortConfig(
			sorting,
			LinkedHashSet(rating),
			translationType,
			minPages
		)
	}
}