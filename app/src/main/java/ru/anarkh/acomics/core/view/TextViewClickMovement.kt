package ru.anarkh.acomics.core.view

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView


/**
 * Утилитарная обертка вокруг [LinkMovementMethod], упрощающая отлов событий нажатия на гиперссылки
 * для простановки соответствующих колбеков на эти события. Как правило - анальных.
 */
class TextViewClickMovement : LinkMovementMethod() {

	var callback: ((linkUrl: String) -> Unit)? = null

	private var gestureDetector: GestureDetector? = null
	private var textView: TextView? = null
	private var spannable: Spannable? = null

	override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
		spannable = buffer
		textView = widget
		var gestureDetector = this.gestureDetector
		if (gestureDetector == null) {
			gestureDetector = GestureDetector(widget.context, SimpleOnGestureListener())
			this.gestureDetector = gestureDetector
		}
		return if (!gestureDetector.onTouchEvent(event)) {
			true
		} else {
			super.onTouchEvent(widget, buffer, event)
		}
	}

	internal inner class SimpleOnGestureListener : GestureDetector.SimpleOnGestureListener() {
		override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
			if (textView == null || spannable == null || callback == null) {
				return false
			}
			val linkText = getLinkText(textView!!, spannable!!, event)
			if (linkText != null) {
				callback?.invoke(linkText)
				return true
			}
			return false
		}

		private fun getLinkText(
			tv: TextView,
			buffer: Spannable,
			event: MotionEvent
		): String? {
			var x = event.x.toInt()
			var y = event.y.toInt()
			x -= tv.totalPaddingLeft
			y -= tv.totalPaddingTop
			x += tv.scrollX
			y += tv.scrollY
			val layout = tv.layout
			val line = layout.getLineForVertical(y)
			val off = layout.getOffsetForHorizontal(line, x.toFloat())
			val link = buffer.getSpans(off, off, ClickableSpan::class.java)
			if (link.isNotEmpty()) {
				val span = link[0]
				return if (span is URLSpan) {
					span.url
				} else buffer.subSequence(
					buffer.getSpanStart(link[0]),
					buffer.getSpanEnd(link[0])
				).toString()
			}
			return null
		}
	}
}