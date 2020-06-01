package ru.anarkh.acomics.comics.repository

import androidx.annotation.WorkerThread
import ru.anarkh.acomics.comics.model.ComicsPage
import ru.anarkh.acomics.core.api.AComicsIssuesService

class ComicsRepository(
	private val comicsIssuesService: AComicsIssuesService
) {

	@WorkerThread
	fun getComicsPage(comicsName: String): ArrayList<ComicsPage> {
		//var page = getCachedPage(pageIndex)
		//if (page != null) {
		//	return page
		//}
		val list = retrieveFromServer(comicsName)
		return list
		// todo сохранять в кэш тут.
	}

	private fun retrieveFromServer(comicsName: String) : ArrayList<ComicsPage> {
		val comicsPage: List<ComicsPage> = comicsIssuesService.issue(comicsName)
			.execute()
			.body()
			?: throw Exception("failed to retrieve comics pages")
		return ArrayList(comicsPage)
	}
}