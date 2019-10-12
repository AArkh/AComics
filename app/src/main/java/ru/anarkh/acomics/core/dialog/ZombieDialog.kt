package ru.anarkh.acomics.core.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ru.arkharov.statemachine.SavedBoolean
import ru.arkharov.statemachine.StateRegistry

/**
 * @param key При использовании нескольких диалогов на одном экране передать уникальный ключ
 */
class ZombieDialog(
	lifecycle: Lifecycle,
	private val context: Context,
	private val dialogFactory: DialogFactory,
	stateRegistry: StateRegistry,
	key: String = "got_shot_in_the_head"
) : DialogWidget, LifecycleObserver {

	override val isShowing: Boolean
		get() = isShowingValue.value

	private val isShowingValue: SavedBoolean = SavedBoolean(key, false)

	private var dialog: Dialog? = null
	private var dismissListener: DialogInterface.OnDismissListener? = null

	init {
		stateRegistry.register(isShowingValue)
		lifecycle.addObserver(this)
	}

	override fun show() {
		if (dialog != null) {
			dialog!!.dismiss()
		}
		dialog = dialogFactory.create(ZombieDialogBuilder(context, this))
		dialog!!.setOnDismissListener(getClosingListener())
		dialog!!.show()
		isShowingValue.value = true
	}

	override fun hide() {
		if (dialog != null) {
			dialog!!.dismiss()
			isShowingValue.value = false
			dialog = null
		}
	}

	/**
	 * Обязательный метод, который вызывает восстановление диалога после поворота экрана.
	 * Вызвать в любое удобное для вас время.
	 */
	fun riseFromTheDead() {
		if (isShowingValue.value) {
			show()
		}
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	fun onDestroy() {
		if (dialog != null) {
			dialog!!.dismiss()
		}
	}

	fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener) {
		this.dismissListener = onDismissListener
	}

	private fun getClosingListener(): DialogInterface.OnDismissListener {
		return DialogInterface.OnDismissListener {
			if (dismissListener != null) {
				dismissListener!!.onDismiss(dialog)
			}
		}
	}
}