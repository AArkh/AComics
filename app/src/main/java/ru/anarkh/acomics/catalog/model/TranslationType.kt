package ru.anarkh.acomics.catalog.model

enum class TranslationType(val queryParam: String) {
	ANY("0"),
	ORIGINAL("orig"),
	TRANSLATED("trans")
}