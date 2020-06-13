package ru.anarkh.acomics.comics.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.res.ResourcesCompat
import ru.anarkh.acomics.R

class ComicsPageIndexWidget(
	private val container: ViewGroup,
	private val indexView: TextView,
	leftArrowButton: View,
	rightArrowButton: View,
	private val seekBar: SeekBar,
	@IntRange(from = 0L) private val maxValue: Int
) {

	var onPageChangedListener: ((newPageIndex: Int) -> Unit)? = null
	var onLeftButtonClickListener: (() -> Unit)? = null
	var onRightButtonClickListener: (() -> Unit)? = null

	private val res = container.resources
	private val showAnimator: ValueAnimator
	private val hideAnimator: ValueAnimator

	init {
		leftArrowButton.setOnClickListener {
			onLeftButtonClickListener?.invoke()
		}
		rightArrowButton.setOnClickListener {
			onRightButtonClickListener?.invoke()
		}
		seekBar.max = maxValue.dec()
		seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onStartTrackingTouch(seekBar: SeekBar) {}
			override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
				updateIndex(seekBar.progress.inc())
			}

			override fun onStopTrackingTouch(seekBar: SeekBar) {
				onPageChangedListener?.invoke(seekBar.progress)
			}
		})
		val animDuration = res.getInteger(android.R.integer.config_shortAnimTime).toLong()
		val transparentAlpha = ResourcesCompat.getFloat(res, R.dimen.full_transparent_alpha)
		val notTransparentAlpha = ResourcesCompat.getFloat(res, R.dimen.not_transparent_alpha)
		hideAnimator = ObjectAnimator.ofFloat(notTransparentAlpha, transparentAlpha)
		hideAnimator.duration = animDuration
		hideAnimator.addUpdateListener { animation: ValueAnimator ->
			container.alpha = animation.animatedValue as Float
		}
		hideAnimator.doOnEnd {
			container.visibility = View.GONE
		}
		showAnimator = ObjectAnimator.ofFloat(transparentAlpha, notTransparentAlpha)
		showAnimator.duration = animDuration
		showAnimator.addUpdateListener { animation: ValueAnimator ->
			container.alpha = animation.animatedValue as Float
		}
		showAnimator.doOnStart {
			container.visibility = View.VISIBLE
		}
	}

	fun hide() {
		if (container.visibility == View.VISIBLE) {
			hideAnimator.start()
		}
	}

	fun show() {
		if (container.visibility == View.GONE) {
			showAnimator.start()
		}
	}

	fun updatePage(newPageIndex: Int) {
		seekBar.progress = newPageIndex
		updateIndex(newPageIndex.inc())
	}

	private fun updateIndex(index: Int) {
		indexView.text = res.getString(
			R.string.comics_index,
			index.toString(),
			maxValue.toString()
		)
	}
}