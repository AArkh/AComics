package ru.anarkh.acomics.comics.repository

import ru.anarkh.acomics.comics.model.ComicsPage

interface ComicsRepository {
	fun getComicsPage(pageIndex: Int): ComicsPage
}