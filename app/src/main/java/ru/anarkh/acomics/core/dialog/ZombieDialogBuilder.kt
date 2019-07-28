package ru.anarkh.acomics.core.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

/**
 * Помогает соблюсти контракт AlertDialog'a, позволяя последнему корректно закрываться по событиям
 * cancel и нажатиям на дефолтные кнопки оного диалога, без явного вызова ZombieDialogWidget.hide()
 */
class ZombieDialogBuilder(
	context: Context,
	private val zombieDialog: ZombieDialog
) : AlertDialog.Builder(context) {

	private var cancelListenerSet: Boolean = false

	override fun create(): AlertDialog {
		if (!cancelListenerSet) {
			super.setOnCancelListener { zombieDialog.hide() }
		}
		return super.create()
	}

	override fun setOnCancelListener(
		onCancelListener: DialogInterface.OnCancelListener
	): AlertDialog.Builder {
		cancelListenerSet = true
		return super.setOnCancelListener { dialog ->
			zombieDialog.hide()
			onCancelListener.onCancel(dialog)
		}
	}

	override fun setPositiveButton(
		@StringRes textId: Int, listener: DialogInterface.OnClickListener
	): AlertDialog.Builder {
		return setPositiveButton(context.getString(textId), listener)
	}

	override fun setPositiveButton(
		text: CharSequence, listener: DialogInterface.OnClickListener
	): AlertDialog.Builder {
		return super.setPositiveButton(text, decorateListener(listener))
	}

	override fun setNegativeButton(
		@StringRes textId: Int, listener: DialogInterface.OnClickListener
	): AlertDialog.Builder {
		return setNegativeButton(context.getString(textId), listener)
	}

	override fun setNegativeButton(
		text: CharSequence, listener: DialogInterface.OnClickListener
	): AlertDialog.Builder {
		return super.setNegativeButton(text, decorateListener(listener))
	}

	override fun setNeutralButton(
		@StringRes textId: Int, listener: DialogInterface.OnClickListener
	): AlertDialog.Builder {
		return setNeutralButton(context.getString(textId), listener)
	}

	override fun setNeutralButton(
		text: CharSequence, listener: DialogInterface.OnClickListener
	): AlertDialog.Builder {
		return super.setNeutralButton(text, decorateListener(listener))
	}

	/**
	 * Добавляет вызов hide() у [zombieDialog] к колбекам.
	 */
	private fun decorateListener(
		listener: DialogInterface.OnClickListener
	): DialogInterface.OnClickListener {
		return DialogInterface.OnClickListener { dialog, which ->
			listener.onClick(dialog, which)
			zombieDialog.hide()
		}
	}
}