package ru.anarkh.acomics.core.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.net.ConnectException

class ExceptionTelemetry(
	private val firebaseCrashlytics: FirebaseCrashlytics
) {

	fun recordException(throwable: Throwable) {
		if (throwable is ConnectException) {
			return
		}
		firebaseCrashlytics.recordException(throwable)
	}
}