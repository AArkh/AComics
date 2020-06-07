package ru.anarkh.acomics.comics.widget

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import com.github.piasy.biv.view.BigImageView

/**
 * [BigImageView] принципиально не предназначен для испоользования в recyclerView:
 * в [onDetachedFromWindow] класс отменяет запрос изображения, а в случае успешной загрузки
 * изображения удаляет [mProgressIndicator]. В итоге при перелистывании не до конца загруженной
 * страницы окончительной загрузки никогда не произойдет.
 */
class BigImageViewExt(
	context: Context,
	attrs: AttributeSet
) : BigImageView(context, attrs) {

	var shownUri: Uri? = null
	var wasDetachedFromWindow: Boolean = false
	var indicator: CustomProgressBar? = null
		private set

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		wasDetachedFromWindow = true
	}

	override fun showImage(thumbnail: Uri?, uri: Uri) {
		super.showImage(thumbnail, uri)
		shownUri = uri
	}

	fun setCustomProgressIndicator(progressIndicator: CustomProgressBar) {
		setProgressIndicator(progressIndicator)
		this.indicator = progressIndicator
	}

	fun shouldResetImageShowing(): Boolean {
		return wasDetachedFromWindow && (indicator?.progressBarView?.parent != null)
	}
}