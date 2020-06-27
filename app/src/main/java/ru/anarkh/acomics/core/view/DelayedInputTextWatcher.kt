package ru.anarkh.acomics.core.view

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher

private const val DEFAULT_INPUT_TO_CALLBACK_DELAY = 1000L

/**
 * Ожидает [inputToCallbackDelay] ms, перед тем как сообщить о результате изменения текстового
 * поля. Позволяет изменять состояние по окончанию ввода пользователя, а не на каждый символ.
 */
class DelayedInputTextWatcher(
	private val userInputCallback: (userInput: String) -> Unit,
	private val inputToCallbackDelay: Long = DEFAULT_INPUT_TO_CALLBACK_DELAY
) : TextWatcher {

	private val handler = Handler(Looper.getMainLooper())
	private var lastInput = 0L

	override fun afterTextChanged(s: Editable?) {
		val inputTime = System.currentTimeMillis()
		lastInput = inputTime
		handler.postDelayed(
			{
				if (inputTime == lastInput) {
					userInputCallback.invoke(s?.toString() ?: "")
				}
			},
			inputToCallbackDelay
		)
	}

	override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
	override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}