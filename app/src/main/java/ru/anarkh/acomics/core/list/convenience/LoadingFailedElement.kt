package ru.anarkh.acomics.core.list.convenience

import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.list.BaseListElement

class LoadingFailedElement : BaseListElement<LoadingFailedElement.Stub>(
	R.layout.loading_failed_item
) {

	var clickListener: (() -> Unit)? = null

	override fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: Stub) {
		holder.itemView.setOnClickListener { clickListener?.invoke() }
	}

	object Stub
}