package ru.anarkh.acomics.comics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.controller.ComicsController
import ru.anarkh.acomics.comics.repository.ComicsHTMLParser
import ru.anarkh.acomics.comics.repository.ComicsRepositoryImpl
import ru.anarkh.acomics.comics.repository.ComicsSessionCache
import ru.anarkh.acomics.comics.widget.ComicsWidget
import ru.anarkh.acomics.core.DefaultActivity
import ru.anarkh.acomics.core.web.HttpClientProvider

class ComicsActivity : DefaultActivity() {

	companion object {
		private const val COMICS_LINK_EXTRA = "comics_link_extra"
		private const val COMICS_PAGES_AMOUNT_EXTRA = "comics_pages_amount_extra"

		fun intent(context: Context, comicsLink: String, pagesAmount: Int) : Intent {
			return Intent(context, ComicsActivity::class.java)
				.putExtra(COMICS_LINK_EXTRA, comicsLink)
				.putExtra(COMICS_PAGES_AMOUNT_EXTRA, pagesAmount)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_comics)

		val comicsLink = intent.getStringExtra(COMICS_LINK_EXTRA)
		val pagesAmount = intent.getIntExtra(COMICS_PAGES_AMOUNT_EXTRA, -1)
		if (comicsLink.isNullOrEmpty() || pagesAmount < 0) {
			finish()
			return
		}

		val widget = ComicsWidget(findViewById(R.id.view_pager), pagesAmount)

		val localCache = ViewModelProviders
			.of(this)
			.get(ComicsSessionCache::class.java)
		val repo = ComicsRepositoryImpl(
			comicsLink,
			HttpClientProvider.getHttpClient(),
			ComicsHTMLParser(),
			localCache
		)

		ComicsController(widget, repo)
	}
}
