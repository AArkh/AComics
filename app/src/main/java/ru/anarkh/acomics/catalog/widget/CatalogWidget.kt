package ru.anarkh.acomics.catalog.widget

import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.catalog.model.CatalogComicsItem

class CatalogWidget(
	list: RecyclerView
) {

	private val adapter = CatalogAdapter()

	init {
		list.layoutManager = LinearLayoutManager(list.context, RecyclerView.VERTICAL, false)
		list.adapter = adapter
	}

	fun updateList(pagedList: PagedList<CatalogComicsItem>) {
		adapter.submitList(pagedList)
	}
}