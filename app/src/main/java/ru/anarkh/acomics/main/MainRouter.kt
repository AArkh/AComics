package ru.anarkh.acomics.main

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import ru.anarkh.acomics.R
import ru.anarkh.acomics.main.catalog.CatalogFragment
import ru.anarkh.acomics.main.favorite.FavoritesFragment
import ru.anarkh.acomics.main.info.InfoFragment

private const val CATALOG_FRAGMENT_TAG = "catalog"
private const val FAVORITES_FRAGMENT_TAG = "favorites"
private const val INFO_FRAGMENT_TAG = "info"

class MainRouter(
	private val fragmentManager: FragmentManager,
	@IdRes private val containerId: Int
) {

	init {
		if (fragmentManager.fragments.isEmpty()) {
			val favoritesFragment = FavoritesFragment()
			val catalogFragment = CatalogFragment()
			val infoFragment = InfoFragment()
			fragmentManager.beginTransaction()
				.add(containerId, favoritesFragment, FAVORITES_FRAGMENT_TAG)
				.add(containerId, catalogFragment, CATALOG_FRAGMENT_TAG)
				.add(containerId, infoFragment, INFO_FRAGMENT_TAG)
				.hide(favoritesFragment)
				.hide(infoFragment)
				.setPrimaryNavigationFragment(catalogFragment)
				.commit()
		}
	}

	fun openCatalog() {
		fragmentManager.beginTransaction()
			.withCrossFadeAnimation()
			.hide(favoriteFragment())
			.hide(infoFragment())
			.show(catalogFragment())
			.setPrimaryNavigationFragment(catalogFragment())
			.commit()
	}

	fun openFavorites() {
		fragmentManager.beginTransaction()
			.withCrossFadeAnimation()
			.hide(catalogFragment())
			.hide(infoFragment())
			.show(favoriteFragment())
			.setPrimaryNavigationFragment(favoriteFragment())
			.commit()
	}

	fun openInfo() {
		fragmentManager.beginTransaction()
			.withCrossFadeAnimation()
			.hide(catalogFragment())
			.hide(favoriteFragment())
			.show(infoFragment())
			.setPrimaryNavigationFragment(infoFragment())
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

	private fun infoFragment() : InfoFragment {
		return fragmentManager.findFragmentByTag(INFO_FRAGMENT_TAG)
			as? InfoFragment
			?: InfoFragment()
	}

	private fun FragmentTransaction.withCrossFadeAnimation() : FragmentTransaction {
		return this.setCustomAnimations(
			R.anim.fade_in,
			R.anim.fade_out,
			R.anim.fade_in,
			R.anim.fade_out
		)
	}
}