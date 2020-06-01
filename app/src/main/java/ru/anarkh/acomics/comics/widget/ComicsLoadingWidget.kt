package ru.anarkh.acomics.comics.widget

import android.view.View
import android.view.ViewGroup

class ComicsLoadingWidget(
	private val container: ViewGroup,
	private val loadingView: View,
	private val retryButton: View
) {

	var onFailedButtonClickListener: (() -> Unit)? = null

	init {
		retryButton.setOnClickListener { onFailedButtonClickListener?.invoke() }
	}

	fun showLoading() {
		container.visibility = View.VISIBLE
		loadingView.visibility = View.VISIBLE
		retryButton.visibility = View.GONE
	}

	fun showFailed() {
		container.visibility = View.VISIBLE
		loadingView.visibility = View.GONE
		retryButton.visibility = View.VISIBLE
	}

	fun hide() {
		container.visibility = View.GONE
	}
}