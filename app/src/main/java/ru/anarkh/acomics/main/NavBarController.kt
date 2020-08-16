package ru.anarkh.acomics.main

class NavBarController(
	private val router: MainRouter,
	widget: NavBarWidget
) {

	init {
		widget.feedItemClickListener = {
			router.openCatalog()
		}
		widget.favoritesItemClickListener = {
			router.openFavorites()
		}
		widget.infoItemClickListener = {
			router.openInfo()
		}
	}
}