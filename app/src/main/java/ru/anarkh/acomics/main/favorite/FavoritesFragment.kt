package ru.anarkh.acomics.main.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.DefaultFragment
import ru.anarkh.acomics.core.Providers
import ru.anarkh.acomics.core.error.ExceptionTelemetry
import ru.anarkh.acomics.main.catalog.CatalogRouter
import ru.anarkh.acomics.main.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.main.catalog.widget.CatalogLoadingWidget
import ru.anarkh.acomics.main.favorite.controller.FavoritesController
import ru.anarkh.acomics.main.favorite.widget.FavoritesWidget

class FavoritesFragment : DefaultFragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_favorites, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val repository = Providers.favoriteRepository
		val widget = FavoritesWidget(
			CatalogLoadingWidget(
				view.findViewById(R.id.loading_screen),
				view.findViewById(R.id.loading_bar),
				view.findViewById(R.id.retry_button),
				view.findViewById(R.id.no_data_text)
			),
			view.findViewById(R.id.list),
			FixedLocaleQuantityStringParser(requireContext())
		)
		FavoritesController(
			CatalogRouter(this),
			repository,
			widget,
			getParentScope(),
			ExceptionTelemetry(FirebaseCrashlytics.getInstance()),
			stateRegistry,
			getViewLifecycle()
		)
	}
}