package ru.anarkh.acomics.core.web

import okhttp3.OkHttpClient

@Deprecated("удалить")
object HttpClientProvider {

	@Volatile
	private var client: OkHttpClient? = null

	fun getHttpClient() : OkHttpClient {
		if (client == null) {
			synchronized(this) {
				if (client == null) {
					client = OkHttpClient.Builder().build()
				}
			}
		}
		return client!!
	}
}