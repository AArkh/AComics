package ru.anarkh.acomics.main

class NavBarController(
	private val router: MainRouter,
	private val widget: NavBarWidget
) {

	init {
		widget.feedItemClickListener = {
			router.openCatalog()
		}
		widget.favoritesItemClickListener = {
			router.openFavorites()
		}
	}
}