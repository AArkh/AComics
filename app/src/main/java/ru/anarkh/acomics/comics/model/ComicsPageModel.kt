package ru.anarkh.acomics.comics.model

import java.io.Serializable

data class ComicsPageModel(
	val comicsTitle: String,
	val imageUrl: String,
	val issueName: String?
) : Serializable