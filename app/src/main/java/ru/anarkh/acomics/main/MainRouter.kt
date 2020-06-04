package ru.anarkh.acomics.main

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import ru.anarkh.acomics.main.catalog.CatalogFragment
import ru.anarkh.acomics.main.favorite.FavoritesFragment

private const val CATALOG_FRAGMENT_TAG = "catalog"
private const val FAVORITES_FRAGMENT_TAG = "favorites"

class MainRouter(
	private val fragmentManager: FragmentManager,
	@IdRes private val containerId: Int
) {

	init {
		if (fragmentManager.fragments.isEmpty()) {
			fragmentManager.beginTransaction()
				.add(containerId, FavoritesFragment(), FAVORITES_FRAGMENT_TAG)
				.add(containerId, CatalogFragment(), CATALOG_FRAGMENT_TAG)
				.commit()
		}
	}

	fun openCatalog() {
		fragmentManager.beginTransaction()
			.hide(favoriteFragment())
			.show(catalogFragment())
			.commit()
	}

	fun openFavorites() {
		fragmentManager.beginTransaction()
			.hide(catalogFragment())
			.show(favoriteFragment())
			.commit()
	}

	private fun catalogFragment() : CatalogFragment {
		return fragmentManager.findFragmentByTag(CATALOG_FRAGMENT_TAG)
			as? CatalogFragment
			?: CatalogFragment()
	}

	private fun favoriteFragment() : FavoritesFragment {
		return fragmentManager.findFragmentByTag(FAVORITES_FRAGMENT_TAG)
			as? FavoritesFragment
			?: FavoritesFragment()
	}
}