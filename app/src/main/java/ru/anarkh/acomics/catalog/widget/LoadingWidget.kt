package ru.anarkh.acomics.catalog.widget

import android.view.View
import android.view.ViewGroup

class LoadingWidget(
	private val container: ViewGroup,
	private val loadingView: View,
	private val retryButton: View,
	private val noDataView: View
) {

	var retryButtonClickListener: View.OnClickListener? = null

	init {
		retryButton.setOnClickListener {
			retryButtonClickListener?.onClick(it)
		}
	}

	fun showNoData() {
		container.visibility = View.VISIBLE
		loadingView.visibility = View.GONE
		retryButton.visibility = View.GONE
		noDataView.visibility = View.VISIBLE
	}

	fun showLoading() {
		container.visibility = View.VISIBLE
		loadingView.visibility = View.VISIBLE
		retryButton.visibility = View.GONE
		noDataView.visibility = View.GONE
	}

	fun showFailed() {
		container.visibility = View.VISIBLE
		loadingView.visibility = View.GONE
		retryButton.visibility = View.VISIBLE
		noDataView.visibility = View.GONE
	}

	fun hide() {
		container.visibility = View.GONE
	}
}