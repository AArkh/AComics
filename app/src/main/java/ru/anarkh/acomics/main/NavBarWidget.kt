package ru.anarkh.acomics.main

import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.anarkh.acomics.R

class NavBarWidget(
	navBar: BottomNavigationView
) {

	var feedItemClickListener: (() -> Unit)? = null
	var favoritesItemClickListener: (() -> Unit)? = null

	init {
		navBar.setOnNavigationItemSelectedListener { item: MenuItem ->
			when(item.itemId) {
				R.id.feed_item -> {
					feedItemClickListener?.invoke()
					true
				}
				R.id.favorites_item -> {
					favoritesItemClickListener?.invoke()
					true
				}
				else -> false
			}
		}
	}
}