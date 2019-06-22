package ru.anarkh.acomics.catalog.util

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.PluralsRes
import androidx.appcompat.view.ContextThemeWrapper
import java.util.*

//todo запихать в какой-нибудь DI-контейнер и юзать синглтоном везде
class FixedLocaleQuantityStringParser(context: Context) {

	private val contextWrapper = ContextThemeWrapper(context, context.theme)

	init {
		val newConfiguration = Configuration(context.resources.configuration)
		newConfiguration.setLocale(Locale("ru"))
		contextWrapper.applyOverrideConfiguration(newConfiguration)
	}

	/**
	 * Посмотреть сурово на эту штуку. Используется криво.
	 */
	fun formatQuantityString(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
		return contextWrapper.resources.getQuantityString(id, quantity, *formatArgs)
	}
}