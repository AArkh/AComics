package ru.anarkh.acomics.core.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MultipleVHListAdapter(
	diffUtilsCallback: DiffUtil.ItemCallback<Any>,
	vararg associations: Pair<BaseListElement<out Any>, Class<*>>
) : ListAdapter<Any, RecyclerView.ViewHolder>(diffUtilsCallback) {

	private val modelClassToElementMap = HashMap<Class<*>, BaseListElement<out Any>>(
		associations.size,
		1f
	)

	init {
		for (association in associations) {
			modelClassToElementMap[association.second] = association.first
		}
	}

	@LayoutRes
	override fun getItemViewType(position: Int): Int {
		val model = getItem(position)
		val element = getListElementFromModel(model::class.java)
		return element.holderLayout
	}

	override fun onCreateViewHolder(
		parent: ViewGroup,
		@LayoutRes viewType: Int
	): RecyclerView.ViewHolder {
		return object : RecyclerView.ViewHolder(
			LayoutInflater.from(parent.context).inflate(viewType, parent, false)
		) {}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val model = getItem(position)
		val element = getListElementFromModel(model::class.java)
		element.onBindViewHolder(holder, position, getItem(position))
	}

	private fun getListElementFromModel(clazz: Class<out Any>): BaseListElement<out Any> {
		return modelClassToElementMap[clazz] as BaseListElement<Any>
	}
}