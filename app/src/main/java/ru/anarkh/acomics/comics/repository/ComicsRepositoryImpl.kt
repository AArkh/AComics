package ru.anarkh.acomics.comics.repository

import android.net.Uri
import android.util.Log
import androidx.annotation.WorkerThread
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.anarkh.acomics.comics.model.ComicsPage

class ComicsRepositoryImpl(
	private val comicsLink: String,
	private val httpClient: OkHttpClient,
	private val parser: ComicsHTMLParser,
	private var sessionCache: ComicsSessionCache
) : ComicsRepository {

	override fun getCachedPage(pageIndex: Int): ComicsPage? {
		return sessionCache.getCachedPage(pageIndex)
	}

	@WorkerThread
	override fun getComicsPage(pageIndex: Int): ComicsPage {
		var page = getCachedPage(pageIndex)
		if (page != null) {
			return page
		}
		page = retrieveFromServer(pageIndex)
		sessionCache.putPage(pageIndex, page)
		return page
	}

	private fun retrieveFromServer(pageIndex: Int) : ComicsPage {
		val uri = Uri.Builder()
			.encodedPath(comicsLink)
			.appendEncodedPath("$pageIndex")
			.build()
		Log.d("12345", "going to $uri")
		val request = Request.Builder()
			.url(uri.toString())
			.build()
		val response = httpClient.newCall(request).execute()
		val body = response.body()?.string() ?: throw IllegalStateException("body is empty!")

		val page = parser.parse(body)
		return ComicsPage(page, pageIndex)
	}
}