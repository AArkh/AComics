package ru.anarkh.acomics.main.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.DefaultFragment
import ru.anarkh.acomics.core.api.AComicsCatalogService
import ru.anarkh.acomics.core.api.Providers
import ru.anarkh.acomics.main.catalog.controller.CatalogController
import ru.anarkh.acomics.main.catalog.repository.CatalogDataSource
import ru.anarkh.acomics.main.catalog.repository.CatalogRepository
import ru.anarkh.acomics.main.catalog.repository.CatalogSortConfigRepository
import ru.anarkh.acomics.main.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.main.catalog.widget.CatalogLoadingWidget
import ru.anarkh.acomics.main.catalog.widget.CatalogWidget
import ru.anarkh.acomics.main.catalog.widget.filter.CatalogFilterDialogWidget
import ru.anarkh.acomics.main.catalog.widget.filter.CatalogSortDialogWidget

class CatalogFragment : DefaultFragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_catalog, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
		toolbar.title = ""
		toolbar.findViewById<ImageView>(R.id.toolbar_logo).setImageResource(R.drawable.logo)

		val loadingWidget = CatalogLoadingWidget(
			view.findViewById(R.id.loading_screen),
			view.findViewById(R.id.loading_bar),
			view.findViewById(R.id.retry_button),
			view.findViewById(R.id.no_data_text)
		)
		val widget = CatalogWidget(
			loadingWidget,
			view.findViewById(R.id.list),
			FixedLocaleQuantityStringParser(requireContext())
		)
		val dataSource = CatalogDataSource(
			Providers.retrofit.create(AComicsCatalogService::class.java)
		)
		val repo = CatalogRepository(dataSource, Providers.favoriteRepository)
		CatalogController(
			CatalogRouter(requireContext()),
			widget,
			CatalogSortDialogWidget(requireContext(), getViewLifecycle(), stateRegistry),
			CatalogFilterDialogWidget(requireContext(), getViewLifecycle(), stateRegistry),
			CatalogSortConfigRepository(requireContext()),
			repo,
			Providers.favoriteRepository,
			getParentScope(),
			stateRegistry
		)
	}
}