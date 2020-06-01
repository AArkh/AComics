package ru.anarkh.acomics.comics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.controller.ComicsController
import ru.anarkh.acomics.comics.repository.ComicsRepository
import ru.anarkh.acomics.comics.widget.ComicsLoadingWidget
import ru.anarkh.acomics.comics.widget.ComicsWidget
import ru.anarkh.acomics.core.DefaultActivity
import ru.anarkh.acomics.core.api.AComicsIssuesService
import ru.anarkh.acomics.core.api.Providers

class ComicsActivity : DefaultActivity() {

	companion object {
		private const val COMICS_TITLE_EXTRA = "comics_title_extra"
		private const val COMICS_PAGES_AMOUNT_EXTRA = "comics_pages_amount_extra"

		fun intent(context: Context, comicsLink: String, pagesAmount: Int) : Intent {
			return Intent(context, ComicsActivity::class.java)
				.putExtra(COMICS_TITLE_EXTRA, comicsLink)
				.putExtra(COMICS_PAGES_AMOUNT_EXTRA, pagesAmount)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_comics)

		val comicsTitle = intent.getStringExtra(COMICS_TITLE_EXTRA)
		val pagesAmount = intent.getIntExtra(COMICS_PAGES_AMOUNT_EXTRA, -1)
		if (comicsTitle.isNullOrEmpty() || pagesAmount < 0) {
			finish()
			return
		}
		val loadingWidget = ComicsLoadingWidget(
			findViewById(R.id.loading_screen),
			findViewById(R.id.loading_bar),
			findViewById(R.id.retry_button)
		)
		val widget = ComicsWidget(findViewById(R.id.view_pager), loadingWidget)
		val repo = ComicsRepository(Providers.retrofit.create(AComicsIssuesService::class.java))
		ComicsController(comicsTitle, widget, repo, activityScope, stateRegistry)
	}
}
