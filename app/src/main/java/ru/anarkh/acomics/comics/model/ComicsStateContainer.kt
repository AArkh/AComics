package ru.anarkh.acomics.comics.model

import androidx.lifecycle.ViewModel

/**
 * Ибо порой [ComicsWidgetState] слишкой объемный для bundle'a.
 */
data class ComicsStateContainer(
	var state: ComicsWidgetState = Initial
) : ViewModel()