package ru.anarkh.acomics.core.web

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Дабы всякие неожиданные странички не вылезали
 */
class AdultCookieJar : CookieJar {

	override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {}
	override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
		return mutableListOf(
			Cookie.Builder()
				.domain("acomics.ru")
				.path("/")
				.name("ageRestrict")
				.value("17")
				.build()
		)
	}
}