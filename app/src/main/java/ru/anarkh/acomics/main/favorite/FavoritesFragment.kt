package ru.anarkh.acomics.main.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.DefaultFragment

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
	}
}