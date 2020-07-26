package ru.anarkh.acomics.comics.widget

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import ru.anarkh.acomics.comics.model.*
import ru.anarkh.acomics.comics.widget.page.ComicsPageListItem
import ru.anarkh.acomics.core.list.MultipleVHListAdapter

class ComicsWidget(
	private val viewPager: ViewPager2,
	private val loadingWidget: ComicsLoadingWidget,
	private val indexWidget: ComicsPageIndexWidget,
	private val toolbarWidget: ComicsToolbarWidget
) {

	var onSingleClickListener: (() -> Unit)? = null
	var onImageLoadedListener: ((page: Int) -> Unit)? = null
	var onImageLoadFailedListener: ((page: Int) -> Unit)? = null

	private val adapter: MultipleVHListAdapter
	private val pageItem = ComicsPageListItem(viewPager.resources)

	init {
		adapter = MultipleVHListAdapter(
			ComicsPagesValidator(),
			pageItem to ComicsPageUiModel::class.java
		)
		viewPager.adapter = adapter
		viewPager.offscreenPageLimit = 2
		indexWidget.onPageChangedListener = { newPageIndex: Int ->
			viewPager.setCurrentItem(newPageIndex, true)
		}
		pageItem.setOnPageClickListener { onSingleClickListener?.invoke() }
		pageItem.setOnPageLoadedListener { page: Int ->
			onImageLoadedListener?.invoke(page)
		}
		pageItem.setOnPageLoadFailedListener { page: Int ->
			onImageLoadFailedListener?.invoke(page)
		}
	}

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

	fun onLeftButtonClickListener(listener: () -> Unit) {
		indexWidget.onLeftButtonClickListener = listener
	}

	fun onRightButtonClickListener(listener: () -> Unit) {
		indexWidget.onRightButtonClickListener = listener
	}

	fun onAddToBookmarksClickListener(listener: () -> Unit) {
		toolbarWidget.onAddToBookmarksClickListener = listener
	}

	private fun showLoading() {
		viewPager.visibility = View.GONE
		loadingWidget.showLoading()
		toolbarWidget.showLoading()
	}

	private fun showFailed() {
		viewPager.visibility = View.GONE
		loadingWidget.showFailed()
		toolbarWidget.showNothing()
	}

	private fun showContent(content: Content) {
		viewPager.visibility = View.VISIBLE
		loadingWidget.hide()
		adapter.submitList(content.issues as List<Any>)
		if (content.isInFullscreen) {
			indexWidget.hide()
			toolbarWidget.hide()
		} else {
			indexWidget.show()
			toolbarWidget.show()
		}
		indexWidget.updatePage(content.currentPage)
		toolbarWidget.showIssueTitle(content)
	}

	fun scrollToPage(page: Int) {
		if (shouldScrollPage(page)) {
			viewPager.setCurrentItem(page, false)
		} else {
			viewPager.currentItem = page
		}
	}

	/**
	 * Костыль. [ViewPager2] не скроллит анимировано на страницы с вызванным onBind. Точнее, он
	 * решает не скроллить их вовсе. В итоге, при открытии комикса с 2-4-й страницы включительно
	 * комикс открывался всегда на первой.
	 */
	private fun shouldScrollPage(pageToBeShown: Int): Boolean {
		return viewPager.currentItem == 0 && pageToBeShown in 1..3
	}
}