package ru.anarkh.acomics.catalog.model

enum class CatalogSortingBy(val queryParamValue: String) {
	BY_DATE("last_updated"),
	BY_SUBS("total_subscribers"),
	BY_ISSUES("total_pages"),
	BY_ALPHABET("title")
}