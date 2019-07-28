package ru.anarkh.acomics.comics.repository

import androidx.annotation.WorkerThread
import ru.anarkh.acomics.comics.model.ComicsPage

interface ComicsRepository {
	fun getCachedPage(pageIndex: Int): ComicsPage?
	@WorkerThread
	fun getComicsPage(pageIndex: Int): ComicsPage
}