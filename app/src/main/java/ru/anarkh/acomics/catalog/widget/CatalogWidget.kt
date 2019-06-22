package ru.anarkh.acomics.catalog.widget

import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.util.FixedLocaleQuantityStringParser

class CatalogWidget(
	list: RecyclerView,
	quantityStringParser: FixedLocaleQuantityStringParser
) {

	private val adapter = CatalogAdapter(quantityStringParser)

	init {
		list.layoutManager = LinearLayoutManager(list.context, RecyclerView.VERTICAL, false)
		list.adapter = adapter
	}

	fun updateList(pagedList: PagedList<CatalogComicsItem>) {
		adapter.submitList(pagedList)
	}

	fun onComicsClick(listener: (link: String) -> Unit) {
		adapter.onItemClickListener = listener
	}
}