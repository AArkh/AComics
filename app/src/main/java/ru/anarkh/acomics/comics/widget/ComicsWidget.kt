package ru.anarkh.acomics.comics.widget

import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.comics_page_item.view.*
import ru.anarkh.acomics.comics.model.*

class ComicsWidget(
	private val viewPager: ViewPager2,
	private val loadingWidget: ComicsLoadingWidget,
	private val indexWidget: ComicsPageIndexWidget,
	private val toolbarWidget: ComicsToolbarWidget
) {

	var onSingleClickListener: (() -> Unit)? = null

	init {
		viewPager.offscreenPageLimit = 2
		indexWidget.onPageChangedListener = { newPageIndex: Int ->
			viewPager.setCurrentItem(newPageIndex, true)
		}
	}

	fun setOnPageChangeListener(listener: (position: Int) -> Unit) {
		viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				super.onPageSelected(position)
				listener.invoke(position)
				fixImageLoadingBug(position)
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
		if (viewPager.adapter == null) {
			val adapter = ComicsPageAdapter(content.issues)
			adapter.onPageClickListener = { onSingleClickListener?.invoke() }
			viewPager.adapter = adapter
		}
		viewPager.currentItem = content.currentPage
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

	/**
	 * Костыль. {@see [BigImageViewExt]}.
	 */
	private fun fixImageLoadingBug(position: Int) {
		val viewHolder: ComicsPageHolder = (viewPager[0] as? RecyclerView)
			?.findViewHolderForAdapterPosition(position)
			as? ComicsPageHolder
			?: return
		val image: BigImageViewExt = viewHolder.itemView.image
		if (image.shouldResetImageShowing()) {
			image.showImage(image.shownUri)
		}
	}
}