package ru.anarkh.acomics.core.list

import android.annotation.SuppressLint

class ListConfig(
	vararg associations: Triple<BaseListElement<out Any, out BaseViewHolder>, Class<out BaseViewHolder>, Class<*>>
) {

	private val modelToElementMap: LinkedHashMap<Class<*>, BaseListElement<out Any, out BaseViewHolder>> =
		LinkedHashMap(associations.size, 1f)
	@SuppressLint("UseSparseArrays")
	private val viewTypeToElementMap =
		HashMap<Int, BaseListElement<out Any, out BaseViewHolder>>(associations.size, 1f)
	private val viewHolderToListElementMap =
		HashMap<Class<out BaseViewHolder>, BaseListElement<out Any, out BaseViewHolder>>(associations.size, 1f)

	init {
		for (association in associations) {
			modelToElementMap[association.third] = association.first
			viewTypeToElementMap[association.first.getViewType()] = association.first
			viewHolderToListElementMap[association.second] = association.first
		}
	}

	fun getViewTypeFromModel(modelClass: Class<*>): Int {
		return modelToElementMap[modelClass]?.getViewType()
			?: throw IllegalStateException("no view type for model class: ${modelClass.simpleName}")
	}

	fun getListElementFromViewType(viewType: Int): BaseListElement<out Any, out BaseViewHolder> {
		return viewTypeToElementMap[viewType]
			?: throw IllegalStateException("no list element for the view type: $viewType")
	}

	fun getListElementFromHolder(holder: BaseViewHolder): BaseListElement<out Any, out BaseViewHolder> {
		return viewHolderToListElementMap[holder::class.java]
			?: throw IllegalStateException("no list element for the holder: ${holder.javaClass.simpleName}")
	}
}