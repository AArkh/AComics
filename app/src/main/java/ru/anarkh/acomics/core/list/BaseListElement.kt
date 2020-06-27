package ru.anarkh.acomics.core.list

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListElement<MODEL>(
	@LayoutRes val holderLayout: Int
) {

	abstract fun onBind(holder: RecyclerView.ViewHolder, position: Int, model: MODEL)

	fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Any) {
		onBind(holder, position, model as MODEL)
	}
}