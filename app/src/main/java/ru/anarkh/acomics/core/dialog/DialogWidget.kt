package ru.anarkh.acomics.core.dialog

interface DialogWidget {

	/**
	 * @return Показан ли в данный момент диалог
	 */
	val isShowing: Boolean

	/**
	 * Показать диалог
	 */
	fun show()

	/**
	 * Спрятать диалог
	 */
	fun hide()
}
