package ru.anarkh.acomics.comics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.controller.ComicsController
import ru.anarkh.acomics.comics.model.ComicsStateContainer
import ru.anarkh.acomics.comics.repository.ComicsRepository
import ru.anarkh.acomics.comics.widget.ComicsLoadingWidget
import ru.anarkh.acomics.comics.widget.ComicsPageIndexWidget
import ru.anarkh.acomics.comics.widget.ComicsToolbarWidget
import ru.anarkh.acomics.comics.widget.ComicsWidget
import ru.anarkh.acomics.core.DefaultActivity
import ru.anarkh.acomics.core.Providers
import ru.anarkh.acomics.core.api.AComicsIssuesService
import ru.anarkh.acomics.core.error.ExceptionTelemetry
import ru.anarkh.acomics.main.catalog.model.CatalogComicsItemWebModel

const val CATALOG_ID_EXTRA = "catalog_id_extra"

private const val COMICS_PAGES_AMOUNT_EXTRA = "comics_pages_amount_extra"
private const val CATALOG_COMICS_EXTRA = "catalog_comics_extra"

class ComicsActivity : DefaultActivity() {

	companion object {
		fun intent(context: Context, catalogId: String, pagesAmount: Int) : Intent {
			return Intent(context, ComicsActivity::class.java)
				.putExtra(CATALOG_ID_EXTRA, catalogId)
				.putExtra(COMICS_PAGES_AMOUNT_EXTRA, pagesAmount)
		}

		fun intent(
			context: Context,
			comicsModel: CatalogComicsItemWebModel
		) : Intent {
			return Intent(context, ComicsActivity::class.java)
				.putExtra(CATALOG_COMICS_EXTRA, comicsModel)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_comics)

		val comicsModel = intent.getSerializableExtra(CATALOG_COMICS_EXTRA)
			as? CatalogComicsItemWebModel
		val catalogId = comicsModel?.catalogId
			?: intent.getStringExtra(CATALOG_ID_EXTRA)
		val totalPages = comicsModel?.totalPages
			?: intent.getIntExtra(COMICS_PAGES_AMOUNT_EXTRA, -1)
		if (catalogId.isNullOrEmpty() || totalPages < 0) {
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
			totalPages
		)
		val widget = ComicsWidget(
			findViewById(R.id.view_pager),
			loadingWidget,
			indexWidget,
			ComicsToolbarWidget(findViewById(R.id.comics_toolbar))
		)
		val repo = ComicsRepository(Providers.retrofit.create(AComicsIssuesService::class.java))
		val stateContainer = ViewModelProvider(this).get(ComicsStateContainer::class.java)
		ComicsController(
			comicsModel,
			catalogId,
			widget,
			repo,
			Providers.favoriteRepository,
			stateContainer,
			coroutineScope,
			ExceptionTelemetry(FirebaseCrashlytics.getInstance()),
			this,
			stateRegistry,
			backButtonController,
			ComicsRouter(this)
		)
	}
}
