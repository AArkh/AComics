package ru.anarkh.acomics.comics.widget

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import ru.anarkh.acomics.comics.model.*

class ComicsWidget(
	private val viewPager: ViewPager2,
	private val loadingWidget: ComicsLoadingWidget
) {

	fun setOnPageChangeListener(listener: (position: Int) -> Unit) {
		viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				super.onPageSelected(position)
				listener.invoke(position)
			}
		})
	}

	fun updateState(state: ComicsWidgetState) {
		when (state) {
			Initial -> showLoading()
			Loading -> showLoading()
			Failed -> showFailed()
			is Content -> showContent(state)
		}
	}

	fun onRetryClickListener(listener: () -> Unit) {
		loadingWidget.onFailedButtonClickListener = listener
	}

	private fun showLoading() {
		viewPager.visibility = View.GONE
		loadingWidget.showLoading()
	}

	private fun showFailed() {
		viewPager.visibility = View.GONE
		loadingWidget.showFailed()
	}

	private fun showContent(content: Content) {
		viewPager.visibility = View.VISIBLE
		loadingWidget.hide()
		if (viewPager.adapter == null) {
			viewPager.adapter = ComicsPageAdapter(content.issues)
		}
		viewPager.currentItem = content.currentPage
	}
}