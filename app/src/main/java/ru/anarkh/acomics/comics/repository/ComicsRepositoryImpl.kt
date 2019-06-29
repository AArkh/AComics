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
	private val parser: ComicsHTMLParser
) : ComicsRepository {

	@WorkerThread
	override fun getComicsPage(pageIndex: Int): ComicsPage {
		return retrieveFromServer(pageIndex)
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

		return parser.parse(body)
	}
}