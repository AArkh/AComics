package ru.anarkh.acomics.core.coroutines

class ObserverBuilder<RESULT_TYPE>(
	private val key: String
) {

	private var loadingCallback: (() -> Unit)? = null
	private var failedCallback: ((Throwable) -> Unit)? = null
	private var successCallback: ((result: RESULT_TYPE) -> Unit)? = null

	fun onLoading(callback: () -> Unit): ObserverBuilder<RESULT_TYPE> {
		this.loadingCallback = callback
		return this
	}

	fun onFailed(callback: (Throwable) -> Unit): ObserverBuilder<RESULT_TYPE> {
		this.failedCallback = callback
		return this
	}

	fun onSuccess(callback: (RESULT_TYPE) -> Unit): ObserverBuilder<RESULT_TYPE> {
		this.successCallback = callback
		return this
	}

	fun build(): Observer<RESULT_TYPE> {
		return Observer(
			key,
			loadingCallback,
			failedCallback,
			successCallback
		)
	}
}