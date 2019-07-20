package ru.anarkh.acomics.core

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

@Suppress("UNCHECKED_CAST")
open class MultipleVHPagedListAdapter(
	diffUtilsCallback: DiffUtil.ItemCallback<Any>,
	vararg listElements: BaseListElement<out Any, out BaseViewHolder>
) : PagedListAdapter<Any, BaseViewHolder>(
	diffUtilsCallback
) {

	private val elementToViewTypeMap: MutableMap<Int, BaseListElement<*, *>>
	private val modelTypeToViewType: MutableMap<Class<*>, Int>
	private val holderToBinderMap: MutableMap<Class<out BaseViewHolder>, BaseListElement<out Any, out BaseViewHolder>>

	init {
		elementToViewTypeMap = HashMap(listElements.size, 1f)
		modelTypeToViewType = HashMap(listElements.size, 1f)
		holderToBinderMap = HashMap(listElements.size, 1f)
		listElements.forEach {
			elementToViewTypeMap[it.getViewType()] = it
			modelTypeToViewType[it.getModelType()] = it.getViewType()
			holderToBinderMap[it.getHolderType()] = it
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
		return elementToViewTypeMap[viewType]?.onCreateViewHolder(parent)
			?: throw IllegalStateException("element for viewtype: $viewType was not defined")
	}

	override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
		val binder = holderToBinderMap[holder::class.java]
			?: throw IllegalStateException("couldn't cast holder: ${holder::class.java.simpleName} to element")
		binder.onBindViewHolder(holder, position, getItem(position))
	}

	override fun getItemViewType(position: Int): Int {
		val model = getItem(position)
		return if (model == null) 0
		else if (!modelTypeToViewType.containsKey(model::class.java)) 0
		else modelTypeToViewType[model::class.java]!!
	}
}