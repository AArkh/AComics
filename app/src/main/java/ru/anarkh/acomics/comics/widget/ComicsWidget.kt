package ru.anarkh.acomics.comics.widget

import android.net.Uri
import com.github.piasy.biv.view.BigImageView
import ru.anarkh.acomics.comics.model.ComicsPage

class ComicsWidget(
	private val zoomableView : BigImageView
) {

	init {
		zoomableView.setProgressIndicator(CustomProgressBar())
	}

	fun setImage(model: ComicsPage) {
		zoomableView.showImage(Uri.parse(model.imageUrl))
	}
}