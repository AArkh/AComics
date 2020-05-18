package ru.anarkh.acomics.core.list.convenience

import android.view.ViewGroup
import ru.anarkh.acomics.core.list.BaseListElement

class LoadingFailedElement : BaseListElement<LoadingFailedElement.Stub, LoadingFailedViewHolder>() {

	override fun getViewType(): Int = 2323321

	override fun onCreateViewHolder(parent: ViewGroup): LoadingFailedViewHolder {
		return LoadingFailedViewHolder(parent)
	}

	override fun onBind(holder: LoadingFailedViewHolder, position: Int, model: Stub) {}

	object Stub
}