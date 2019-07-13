package ru.anarkh.acomics.comics.controller

import android.util.Log
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.anarkh.acomics.comics.model.ComicsPage
import ru.anarkh.acomics.comics.repository.ComicsRepository
import ru.anarkh.acomics.comics.widget.ComicsWidget

class ComicsController(
	private val widget: ComicsWidget,
	private val repo: ComicsRepository
) {
	init {
		val obser = PublishSubject.create<ComicsPage>()
		obser.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(object : Observer<ComicsPage> {
				override fun onComplete() {}
				override fun onSubscribe(d: Disposable) {}

				override fun onNext(t: ComicsPage) {
					widget.setPage(t.index, t.comicsPageData)
				}

				override fun onError(e: Throwable) {
					Log.e("12345", "onError")
				}
			})

		widget.setOnPageChangeListener { position ->
			Schedulers.io().createWorker().schedule {
				obser.onNext(repo.getComicsPage(position + 1))
			}
		}
	}
}