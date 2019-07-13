package ru.anarkh.acomics.comics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import okhttp3.OkHttpClient
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.controller.ComicsController
import ru.anarkh.acomics.comics.repository.ComicsHTMLParser
import ru.anarkh.acomics.comics.repository.ComicsRepositoryImpl
import ru.anarkh.acomics.comics.repository.ComicsSessionCache
import ru.anarkh.acomics.comics.widget.ComicsWidget

class ComicsActivity : AppCompatActivity() {

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
		if (TextUtils.isEmpty(comicsLink) || pagesAmount < 0) {
			finish()
			return
		}

		Log.d("12345", "pages amount: $pagesAmount")
		val widget = ComicsWidget(findViewById(R.id.view_pager), pagesAmount)

		val localCache = ViewModelProviders
			.of(this)
			.get(ComicsSessionCache::class.java)
		val repo = ComicsRepositoryImpl(comicsLink, OkHttpClient(), ComicsHTMLParser(), localCache)

		ComicsController(widget, repo)
	}
}
