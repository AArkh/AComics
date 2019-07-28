package ru.anarkh.acomics.core.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.anarkh.acomics.R

/**
 * Исправлено:
 * 1. Расчет высоты диалога в landscape-ориентации.
 * 2. Обновление высоты диалога при изменении размеров содержимого.
 */
class FixedBottomSheetDialog(
	context: Context,
	@StyleRes theme: Int = R.style.BottomSheetDialog
) : BottomSheetDialog(context, theme) {

	private var view: View? = null
	private var heightFixOnShowListener: DialogInterface.OnShowListener? = null
	private var onShowListener: DialogInterface.OnShowListener? = null

	init {
		addOnShowListener()
	}

	override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
		super.setContentView(view, params)
		initFixListener(view)
	}

	override fun setContentView(view: View) {
		setContentView(view, null)
	}

	override fun setContentView(@LayoutRes layoutResId: Int) {
		setContentView(layoutInflater.inflate(layoutResId, null, false))
	}

	override fun setOnShowListener(listener: DialogInterface.OnShowListener?) {
		onShowListener = listener
	}

	/**
	 * Вызывать для обновления высоты диалога при изменении содержимого. Например, при
	 * someDialogElement.setVisibility(VISIBLE);
	 */
	fun updateHeight() {
		view!!.post { heightFixOnShowListener!!.onShow(this) }
	}

	private fun addOnShowListener() {
		setOnShowListener { dialog ->
			if (heightFixOnShowListener != null) {
				heightFixOnShowListener!!.onShow(dialog)
			}
			if (onShowListener != null) {
				onShowListener!!.onShow(dialog)
			}
		}
	}

	/**
	 * [BottomSheetDialog] не умеет в раскрытие в landscape-режиме, этот метод лечит такое поведение.
	 */
	private fun initFixListener(view: View) {
		this.view = view
		val behavior = BottomSheetBehavior.from(view.parent as View)
		heightFixOnShowListener = DialogInterface.OnShowListener { behavior.peekHeight = view.height }
	}
}