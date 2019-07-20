package ru.anarkh.acomics.core

import android.view.ViewGroup

abstract class BaseListElement<MODEL, HOLDER: BaseViewHolder>(
	private val modelClass : Class<MODEL>,
	private val holderClass: Class<HOLDER>
) {

	fun getModelType() : Class<MODEL> = modelClass
	fun getHolderType() : Class<HOLDER> = holderClass
	fun <H, M> onBindViewHolder(holder: H, position: Int, model: M) {
		onBind(holder as HOLDER, position, model as MODEL)
	}

	abstract fun getViewType(): Int
	abstract fun onCreateViewHolder(parent: ViewGroup): HOLDER
	abstract fun onBind(holder: HOLDER, position: Int, model: MODEL)
}