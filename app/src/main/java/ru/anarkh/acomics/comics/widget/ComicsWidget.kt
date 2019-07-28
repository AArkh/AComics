package ru.anarkh.acomics.comics.widget

import androidx.viewpager2.widget.ViewPager2
import ru.anarkh.acomics.comics.model.ComicsPageData

class ComicsWidget(
	private val viewPager: ViewPager2,
	pagesCount: Int
) {

	private val adapter = ComicsPageAdapter(pagesCount)

	init {
		viewPager.adapter = adapter
	}

	fun setPage(pageIndex: Int, model: ComicsPageData) {
		adapter.putPage(pageIndex - 1, model)
	}

	fun setCurrentIndex(currentIndex: Int) {
		viewPager.currentItem = currentIndex
	}

	fun setOnPageChangeListener(listener: (position: Int) -> Unit) {
		viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				super.onPageSelected(position)
				listener.invoke(position)
			}
		})
	}
}