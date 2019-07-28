package ru.anarkh.acomics.core.list

import android.view.ViewGroup

abstract class BaseListElement<MODEL, HOLDER: BaseViewHolder> {

	fun <H, M> onBindViewHolder(holder: H, position: Int, model: M) {
		onBind(holder as HOLDER, position, model as MODEL)
	}

	abstract fun getViewType(): Int
	abstract fun onCreateViewHolder(parent: ViewGroup): HOLDER
	abstract fun onBind(holder: HOLDER, position: Int, model: MODEL)
}