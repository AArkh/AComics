package ru.anarkh.acomics.catalog.model

enum class CatalogSortingBy(val queryParamValue: String) {
	BY_DATE("last_update"),
	BY_SUBS("subscr_count"),
	BY_ISSUES("issue_count"),
	BY_ALPHABET("serial_name")
}