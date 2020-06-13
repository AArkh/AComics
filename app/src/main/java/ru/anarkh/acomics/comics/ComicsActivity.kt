package ru.anarkh.acomics.comics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.controller.ComicsController
import ru.anarkh.acomics.comics.repository.ComicsRepository
import ru.anarkh.acomics.comics.widget.ComicsLoadingWidget
import ru.anarkh.acomics.comics.widget.ComicsPageIndexWidget
import ru.anarkh.acomics.comics.widget.ComicsToolbarWidget
import ru.anarkh.acomics.comics.widget.ComicsWidget
import ru.anarkh.acomics.core.DefaultActivity
import ru.anarkh.acomics.core.Providers
import ru.anarkh.acomics.core.api.AComicsIssuesService

class ComicsActivity : DefaultActivity() {

	companion object {
		private const val CATALOG_ID_EXTRA = "catalog_id_extra"
		private const val COMICS_PAGES_AMOUNT_EXTRA = "comics_pages_amount_extra"

		fun intent(context: Context, catalogId: String, pagesAmount: Int) : Intent {
			return Intent(context, ComicsActivity::class.java)
				.putExtra(CATALOG_ID_EXTRA, catalogId)
				.putExtra(COMICS_PAGES_AMOUNT_EXTRA, pagesAmount)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_comics)

		val catalogId = intent.getStringExtra(CATALOG_ID_EXTRA)
		val pagesAmount = intent.getIntExtra(COMICS_PAGES_AMOUNT_EXTRA, -1)
		if (catalogId.isNullOrEmpty() || pagesAmount < 0) {
			finish()
			return
		}
		val loadingWidget = ComicsLoadingWidget(
			findViewById(R.id.loading_screen),
			findViewById(R.id.loading_bar),
			findViewById(R.id.retry_button)
		)
		val indexWidget = ComicsPageIndexWidget(
			findViewById(R.id.index_widget_container),
			findViewById(R.id.index_indicator),
			findViewById(R.id.left_control),
			findViewById(R.id.right_control),
			findViewById(R.id.seek_bar),
			pagesAmount
		)
		val widget = ComicsWidget(
			findViewById(R.id.view_pager),
			loadingWidget,
			indexWidget,
			ComicsToolbarWidget(findViewById(R.id.comics_toolbar))
		)
		val repo = ComicsRepository(Providers.retrofit.create(AComicsIssuesService::class.java))
		ComicsController(
			catalogId,
			widget,
			repo,
			Providers.favoriteRepository,
			coroutineScope,
			FirebaseCrashlytics.getInstance(),
			stateRegistry
		)
	}
}
