package ru.anarkh.acomics.core.coroutines

import com.facebook.common.util.HashCodeUtil

class Observer<RESULT_TYPE>(
	val key: String,
	private val loadingListener: (() -> Unit)? = null,
	private val failedCallback: ((Throwable) -> Unit)? = null,
	private val successCallback: ((result: RESULT_TYPE) -> Unit)? = null
) {

	fun onLoading() {
		loadingListener?.invoke()
	}

	fun onError(throwable: Throwable) {
		failedCallback?.invoke(throwable)
	}

	fun onSuccess(result: RESULT_TYPE) {
		successCallback?.invoke(result)
	}

	override fun equals(other: Any?): Boolean {
		return other is Observer<*> && key == other.key
	}

	override fun hashCode(): Int {
		return HashCodeUtil.hashCode(key)
	}
}