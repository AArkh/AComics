package ru.anarkh.acomics.core.coroutines

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class ActivityScope : ViewModel(), CoroutineScope by MainScope() {

	private val observers: MutableMap<String, Observer<*>> = HashMap()
	private val jobs: MutableMap<String, Job> = HashMap()

	fun addObserver(observer: Observer<*>) {
		observers[observer.key] = observer
		val job = jobs[observer.key] ?: return
		if (job.isActive) {
			observer.onLoading()
		}
	}

	fun <TYPE> runCoroutine(key: String, block: suspend CoroutineScope.() -> TYPE) {
		observers[key]?.onLoading()
		val job: Job = launch {
			try {
				val result: TYPE = withContext(Dispatchers.IO, block)
				val observer = observers[key] as? Observer<TYPE> ?: return@launch
				observer.onSuccess(result)
			} catch (e: Exception) {
				val observer = observers[key] as? Observer<TYPE> ?: return@launch
				observer.onError(e)
				e.printStackTrace()
			}
		}
		jobs[key] = job
	}

	fun cancelScope() {
		observers.clear()
		jobs.clear()
		cancel()
	}

	fun removeObservers() {
		observers.clear()
	}
}

