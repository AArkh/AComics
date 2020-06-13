package ru.anarkh.acomics.comics.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.res.ResourcesCompat
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.model.Content

class ComicsToolbarWidget(
	private val toolbar: Toolbar
) {

	var onAddToBookmarksClickListener: (() -> Unit)? = null

	init {
		toolbar.setOnMenuItemClickListener { item: MenuItem ->
			if (item.itemId == R.id.bookmark_item) {
				onAddToBookmarksClickListener?.invoke()
			}
			return@setOnMenuItemClickListener true
		}
	}

	fun hide() {
		if (toolbar.visibility == View.VISIBLE) {
			toolbar.getAnimator(false).start()
		}
	}

	fun show() {
		if (toolbar.visibility == View.GONE) {
			toolbar.getAnimator(true).start()
		}
	}

	fun showIssueTitle(content: Content) {
		toolbar.title = content.issues[content.currentPage].issueName
	}

	fun showNothing() {
		toolbar.title = ""
	}

	fun showLoading() {
		toolbar.setTitle(R.string.comics_toolbar_loading)
	}

	private fun Toolbar.getAnimator(isShowAnimator: Boolean): ValueAnimator {
		val animDuration = context.resources.getInteger(
			android.R.integer.config_shortAnimTime
		).toLong()
		val notTransparentAlpha = ResourcesCompat.getFloat(
			context.resources,
			R.dimen.not_transparent_alpha
		)
		val transparentAlpha = ResourcesCompat.getFloat(
			context.resources,
			R.dimen.full_transparent_alpha
		)
		val animator: ValueAnimator = if (isShowAnimator) {
			ObjectAnimator.ofFloat(transparentAlpha, notTransparentAlpha)
		} else {
			ObjectAnimator.ofFloat(notTransparentAlpha, transparentAlpha)
		}
		animator.duration = animDuration
		animator.addUpdateListener { animation: ValueAnimator ->
			alpha = animation.animatedValue as Float
		}
		if (isShowAnimator) {
			animator.doOnStart {
				visibility = View.VISIBLE
			}
		} else {
			animator.doOnEnd {
				visibility = View.GONE
			}
		}
		return animator
	}
}