package ru.anarkh.acomics.core.list

import android.view.ViewGroup

class LoadingElement : BaseListElement<Unit, LoadingViewHolder>() {
	override fun getViewType(): Int = 0

	override fun onCreateViewHolder(parent: ViewGroup): LoadingViewHolder =
		LoadingViewHolder(parent)

	override fun onBind(holder: LoadingViewHolder, position: Int, model: Unit) {}
}