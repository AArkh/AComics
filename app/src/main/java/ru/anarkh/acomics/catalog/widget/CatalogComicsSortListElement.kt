package ru.anarkh.acomics.catalog.widget

import android.view.ViewGroup
import kotlinx.android.synthetic.main.catalog_sort_item.view.*
import ru.anarkh.acomics.catalog.model.CatalogSortModel
import ru.anarkh.acomics.core.list.BaseListElement

class CatalogComicsSortListElement : BaseListElement<CatalogSortModel, CatalogSortItemViewHolder>() {
	override fun getViewType(): Int = 1552

	override fun onCreateViewHolder(parent: ViewGroup): CatalogSortItemViewHolder = CatalogSortItemViewHolder(parent)

	override fun onBind(holder: CatalogSortItemViewHolder, position: Int, model: CatalogSortModel) {
		holder.itemView.sort_order_spinner.text = model.x
	}
}