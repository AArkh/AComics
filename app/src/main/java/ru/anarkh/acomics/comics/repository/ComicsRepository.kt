package ru.anarkh.acomics.comics.repository

import androidx.annotation.WorkerThread
import ru.anarkh.acomics.comics.model.ComicsPageModel
import ru.anarkh.acomics.core.api.AComicsIssuesService

class ComicsRepository(
	private val comicsIssuesService: AComicsIssuesService
) {

	@WorkerThread
	fun getComicsPage(catalogId: String): ArrayList<ComicsPageModel> {
		//var page = getCachedPage(pageIndex)
		//if (page != null) {
		//	return page
		//}
		val list = retrieveFromServer(catalogId)
		return list
		// todo сохранять в кэш тут.
	}

	private fun retrieveFromServer(catalogId: String) : ArrayList<ComicsPageModel> {
		val comicsPage: List<ComicsPageModel> = comicsIssuesService.issue(catalogId)
			.execute()
			.body()
			?: throw Exception("failed to retrieve comics pages")
		return ArrayList(comicsPage)
	}
}