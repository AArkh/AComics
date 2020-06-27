package ru.anarkh.acomics.core.error

private const val PACKAGE = "acomics"
private const val EXTRAS_PATH = "path"
private const val EXTRAS_LINE = "line"
private const val EXTRAS_EXCEPTION = "exception"
private const val EXTRAS_DETAIL = "detail"

class StackTraceHelper {

	fun getThrowableExtras(throwable: Throwable): MutableMap<String, String> {
		val scapegoat: StackTraceElement = getScapegoat(throwable)
		val extras = HashMap<String, String>(4, 1f)
		extras[EXTRAS_PATH] = scapegoat.className ?: "failed to retrieve className"
		extras[EXTRAS_LINE] = scapegoat.lineNumber.toString() ?: "failed to retrieve lineNumber"
		extras[EXTRAS_EXCEPTION] = throwable.javaClass.canonicalName
			?: throwable.javaClass.name ?: "failed to retrieve throwable name"
		extras[EXTRAS_DETAIL] = throwable.localizedMessage
			?: throwable.message ?: "failed to retrieve message"
		return extras
	}

	/**
	 * Ищем [StackTraceElement], который будем выводить в исключениях как причину взрыва.
	 * Естественно, нас интересует [PACKAGE], однако позже можно добавить и
	 * какие-то библиотечные пакеты, чтобы отличать взрыв системы от взрыва, допустим, фрески.
	 */
	private fun getScapegoat(throwable: Throwable): StackTraceElement {
		// Ищем виноватого среди своих. Если нет, то наказываем первого попавшегося.
		return throwable.stackTrace.find { suspect: StackTraceElement ->
			return@find suspect.className.contains(PACKAGE)
		} ?: throwable.stackTrace.first()
	}
}