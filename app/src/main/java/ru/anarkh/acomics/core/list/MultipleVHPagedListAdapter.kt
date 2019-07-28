package ru.anarkh.acomics.core.list

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

@Suppress("UNCHECKED_CAST")
open class MultipleVHPagedListAdapter(
	diffUtilsCallback: DiffUtil.ItemCallback<Any>,
	private val listMapper: ListMapper
) : PagedListAdapter<Any, BaseViewHolder>(
	diffUtilsCallback
) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
		return listMapper.getListElementFromViewType(viewType).onCreateViewHolder(parent)
	}

	override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
		listMapper.getListElementFromHolder(holder).onBindViewHolder(holder, position, getItem(position))
	}

	override fun getItemViewType(position: Int): Int {
		val model = getItem(position)
		return if (model == null) Int.MIN_VALUE
		else listMapper.getViewTypeFromModel(model::class.java)
	}
}