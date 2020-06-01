package ru.anarkh.acomics.catalog

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import ru.anarkh.acomics.R
import ru.anarkh.acomics.catalog.controller.CatalogController
import ru.anarkh.acomics.catalog.repository.CatalogDataSource
import ru.anarkh.acomics.catalog.repository.CatalogRepository
import ru.anarkh.acomics.catalog.repository.CatalogSortConfigRepository
import ru.anarkh.acomics.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.catalog.widget.CatalogLoadingWidget
import ru.anarkh.acomics.catalog.widget.CatalogWidget
import ru.anarkh.acomics.catalog.widget.filter.CatalogFilterDialogWidget
import ru.anarkh.acomics.catalog.widget.filter.CatalogSortDialogWidget
import ru.anarkh.acomics.core.DefaultActivity
import ru.anarkh.acomics.core.api.AComicsCatalogService
import ru.anarkh.acomics.core.api.Providers

class CatalogActivity : DefaultActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		toolbar.title = ""
		toolbar.findViewById<ImageView>(R.id.toolbar_logo).setImageResource(R.drawable.logo)
		setSupportActionBar(toolbar)

		val loadingWidget = CatalogLoadingWidget(
			findViewById(R.id.loading_screen),
			findViewById(R.id.loading_bar),
			findViewById(R.id.retry_button),
			findViewById(R.id.no_data_text)
		)
		val widget = CatalogWidget(
			loadingWidget,
			findViewById(R.id.list),
			FixedLocaleQuantityStringParser(this)
		)
		val dataSource = CatalogDataSource(
			Providers.retrofit.create(AComicsCatalogService::class.java)
		)
		val repo = CatalogRepository(dataSource)
		CatalogController(
			CatalogRouter(this),
			widget,
			CatalogSortDialogWidget(this, lifecycle, stateRegistry),
			CatalogFilterDialogWidget(this, lifecycle, stateRegistry),
			CatalogSortConfigRepository(this),
			repo,
			activityScope,
			stateRegistry
		)
	}
}
