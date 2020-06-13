package ru.anarkh.acomics.comics.repository

import androidx.annotation.WorkerThread
import ru.anarkh.acomics.comics.model.ComicsPageModel
import ru.anarkh.acomics.core.api.AComicsIssuesService

class ComicsRepository(
	private val comicsIssuesService: AComicsIssuesService
) {

	@WorkerThread
	fun getComicsPages(catalogId: String): ArrayList<ComicsPageModel> {
		// cache here later
		return retrieveFromServer(catalogId)
	}

	private fun retrieveFromServer(catalogId: String) : ArrayList<ComicsPageModel> {
		val comicsPage: List<ComicsPageModel> = comicsIssuesService.issue(catalogId)
			.execute()
			.body()
			?: throw Exception("failed to retrieve comics pages")
		return ArrayList(comicsPage)
	}
}