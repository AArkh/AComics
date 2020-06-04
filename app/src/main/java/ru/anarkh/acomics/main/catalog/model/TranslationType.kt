package ru.anarkh.acomics.main.catalog.model

enum class TranslationType(val queryParam: String) {
	ANY("0"),
	ORIGINAL("orig"),
	TRANSLATED("trans")
}