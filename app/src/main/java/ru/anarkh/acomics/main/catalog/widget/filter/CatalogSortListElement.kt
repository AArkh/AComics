package ru.anarkh.acomics.main.catalog.widget.filter

import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.catalog_sort_item.view.*
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.list.BaseListElement
import ru.anarkh.acomics.main.catalog.model.CatalogSortConfig

class CatalogSortListElement : BaseListElement<CatalogSortConfig>(R.layout.catalog_sort_item) {

	var onFilterItemClickListener: (() -> Unit)? = null
	var onSortIconClickListener: (() -> Unit)? = null

	override fun onBind(
		holder: RecyclerView.ViewHolder,
		position: Int,
		model: CatalogSortConfig
	) {
		holder.itemView.filter_item.setOnClickListener {
			onFilterItemClickListener?.invoke()
		}
		holder.itemView.sort_icon.setOnClickListener {
			onSortIconClickListener?.invoke()
		}
	}
}