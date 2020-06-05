package ru.anarkh.acomics.core.coroutines

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class ObservableScope : ViewModel(), CoroutineScope by MainScope() {

	private val observers: MutableMap<String, MutableList<Observer<*>>> = HashMap()
	private val jobs: MutableMap<String, Job> = HashMap()

	fun addObserver(observer: Observer<*>) {
		if (!observers.contains(observer.key)) {
			observers[observer.key] = mutableListOf()
		}
		val list = observers[observer.key]
		list?.add(observer)
		val job = jobs[observer.key] ?: return
		if (job.isActive) {
			observer.onLoading()
		}
	}

	/**
	 * by default run on [Dispatchers.IO] dispatcher.
	 */
	fun <TYPE> runObservable(key: String, block: suspend CoroutineScope.() -> TYPE) {
		observers[key]?.forEach { it.onLoading() }
		val job: Job = launch {
			try {
				val result: TYPE = withContext(Dispatchers.IO, block)
				val observers = observers[key] as? List<Observer<TYPE>> ?: return@launch
				observers.forEach { it.onSuccess(result) }
			} catch (e: Exception) {
				val observers = observers[key] as? List<Observer<TYPE>> ?: return@launch
				observers.forEach { it.onError(e) }
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

