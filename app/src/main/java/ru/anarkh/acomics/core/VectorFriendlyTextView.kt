package ru.anarkh.acomics.core

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import ru.anarkh.acomics.R

/**
 * Текствьюха, корректно обрабатывающая DrawableLeft и прочие подобные параметры при использовании
 * векторных изображений(включенного параметра vectorDrawables.useSupportLibrary в граддле)
 *
 * Использовать очень просто - вместо android:drawableLeft следует задействовать app:vectorDrawableLeft
 */
open class VectorFriendlyTextView : AppCompatTextView {

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		initAttrs(context, attrs)
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		initAttrs(context, attrs)
	}

	fun setVectorDrawableLeft(@DrawableRes drawableRes: Int) {
		val drawable = AppCompatResources.getDrawable(context, drawableRes)
		TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
			this, drawable, null, null, null
		)
	}

	internal fun initAttrs(context: Context, attrs: AttributeSet?) {
		if (attrs != null) {
			val attributeArray = context.obtainStyledAttributes(
				attrs,
				R.styleable.VectorFriendlyTextView
			)

			var drawableStart: Drawable? = null
			var drawableEnd: Drawable? = null
			var drawableBottom: Drawable? = null
			var drawableTop: Drawable? = null

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				drawableStart = attributeArray.getDrawable(
					R.styleable.VectorFriendlyTextView_vectorDrawableLeft
				)
				drawableEnd = attributeArray.getDrawable(
					R.styleable.VectorFriendlyTextView_vectorDrawableRight
				)
				drawableBottom = attributeArray.getDrawable(
					R.styleable.VectorFriendlyTextView_vectorDrawableBottom
				)
				drawableTop = attributeArray.getDrawable(
					R.styleable.VectorFriendlyTextView_vectorDrawableTop
				)
			} else {
				val drawableStartId = attributeArray.getResourceId(
					R.styleable.VectorFriendlyTextView_vectorDrawableLeft, -1
				)
				val drawableEndId = attributeArray.getResourceId(
					R.styleable.VectorFriendlyTextView_vectorDrawableRight, -1
				)
				val drawableBottomId = attributeArray.getResourceId(
					R.styleable.VectorFriendlyTextView_vectorDrawableBottom, -1
				)
				val drawableTopId = attributeArray.getResourceId(
					R.styleable.VectorFriendlyTextView_vectorDrawableTop, -1
				)

				if (drawableStartId != -1) {
					drawableStart = AppCompatResources.getDrawable(
						context, drawableStartId
					)
				}
				if (drawableEndId != -1) {
					drawableEnd = AppCompatResources.getDrawable(
						context, drawableEndId
					)
				}
				if (drawableBottomId != -1) {
					drawableBottom = AppCompatResources.getDrawable(
						context, drawableBottomId
					)
				}
				if (drawableTopId != -1) {
					drawableTop = AppCompatResources.getDrawable(context, drawableTopId)
				}
			}
			TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
				this, drawableStart, drawableTop, drawableEnd, drawableBottom
			)
			attributeArray.recycle()
		}
	}
}
