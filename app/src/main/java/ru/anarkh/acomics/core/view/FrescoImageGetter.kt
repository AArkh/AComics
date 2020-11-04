package ru.anarkh.acomics.core.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Html
import android.util.Log
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.image.ImageUploader

class FrescoImageGetter(
	private val textView: TextView,
	private val imageUploader: ImageUploader
) : Html.ImageGetter {

	override fun getDrawable(source: String?): Drawable? {
		Log.d("12345", "gettingDrawable for $source")
		if (source == null) {
			return textView.context.getDrawableWithIntrinsicBounds(R.drawable.ic_broken_image)
		}
		val loadingDrawable = textView.context.getDrawableWithIntrinsicBounds(R.drawable.ic_image_search)

		imageUploader.loadBitmap(Uri.parse(source), 40, 40)
		return loadingDrawable
	}

	private fun Context.getDrawableWithIntrinsicBounds(@DrawableRes drawableRes: Int): Drawable? {
		val drawable = ContextCompat.getDrawable(this, drawableRes) ?: return null
		drawable.setBounds(0, 0, drawable.intrinsicHeight, drawable.intrinsicWidth)
		return drawable
	}
}