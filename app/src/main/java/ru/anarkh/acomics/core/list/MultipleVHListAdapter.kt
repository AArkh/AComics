package ru.anarkh.acomics.core.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

open class MultipleVHListAdapter(
	diffUtilsCallback: DiffUtil.ItemCallback<Any>,
	private val listConfig: ListConfig
) : ListAdapter<Any, BaseViewHolder>(diffUtilsCallback) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
		return listConfig.getListElementFromViewType(viewType).onCreateViewHolder(parent)
	}

	override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
		listConfig.getListElementFromHolder(holder)
			.onBindViewHolder(holder, position, getItem(position))
	}

	override fun getItemViewType(position: Int): Int {
		val model = getItem(position)
		return listConfig.getViewTypeFromModel(model::class.java)
	}
}