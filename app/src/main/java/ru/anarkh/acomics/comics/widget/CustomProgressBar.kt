package ru.anarkh.acomics.comics.widget

import android.view.LayoutInflater
import android.view.View
import com.daimajia.numberprogressbar.NumberProgressBar
import com.github.piasy.biv.indicator.ProgressIndicator
import com.github.piasy.biv.view.BigImageView
import ru.anarkh.acomics.R

class CustomProgressBar : ProgressIndicator {

	private lateinit var progressBarView : NumberProgressBar

	override fun getView(parent: BigImageView): View {
		progressBarView = LayoutInflater.from(parent.context)
			.inflate(R.layout.comics_custom_progress_bar_indicator, parent, false)
			as NumberProgressBar
		return progressBarView
	}

	override fun onProgress(progress: Int) {
		progressBarView.progress = progress
	}

	override fun onFinish() {}
	override fun onStart() {}
}