package ru.anarkh.acomics.comics.repository

import android.util.SparseArray
import androidx.core.util.contains
import androidx.lifecycle.ViewModel
import ru.anarkh.acomics.comics.model.ComicsPage

class ComicsSessionCache : ViewModel() {

	private val pagesList = SparseArray<ComicsPage?>()

	fun getCachedPage(pageIndex: Int): ComicsPage? {
		return if (pagesList.contains(pageIndex)) {
			pagesList[pageIndex]
		} else null
	}

	fun putPage(pageIndex: Int, pageData: ComicsPage) {
		pagesList.put(pageIndex, pageData)
	}
}