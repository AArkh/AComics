package ru.anarkh.acomics.core.list.convenience

import android.view.ViewGroup
import ru.anarkh.acomics.core.list.BaseListElement

class LoadingElement : BaseListElement<LoadingElement.Stub, LoadingViewHolder>() {

	override fun getViewType(): Int = 0

	override fun onCreateViewHolder(parent: ViewGroup): LoadingViewHolder {
		return LoadingViewHolder(parent)
	}

	override fun onBind(holder: LoadingViewHolder, position: Int, model: Stub) {}

	object Stub
}