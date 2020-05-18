package ru.anarkh.acomics.catalog.widget.filter

import android.view.ViewGroup
import kotlinx.android.synthetic.main.catalog_sort_item.view.*
import ru.anarkh.acomics.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.core.list.BaseListElement

class CatalogSortListElement : BaseListElement<CatalogSortConfig, CatalogSortItemViewHolder>() {

	var onFilterItemClickListener: (() -> Unit)? = null
	var onSortIconClickListener: (() -> Unit)? = null

	override fun getViewType(): Int = 1552
	override fun onCreateViewHolder(parent: ViewGroup): CatalogSortItemViewHolder {
		val holder =
			CatalogSortItemViewHolder(
				parent
			)
		holder.itemView.filter_item.setOnClickListener {
			onFilterItemClickListener?.invoke()
		}
		holder.itemView.sort_icon.setOnClickListener {
			onSortIconClickListener?.invoke()
		}
		return holder
	}

	override fun onBind(holder: CatalogSortItemViewHolder, position: Int, config: CatalogSortConfig) {}
}