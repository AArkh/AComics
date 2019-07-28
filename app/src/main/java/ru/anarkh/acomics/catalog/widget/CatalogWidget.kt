package ru.anarkh.acomics.catalog.widget

import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.catalog.controller.CatalogItemsDiffsValidator
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.model.CatalogSortConfig
import ru.anarkh.acomics.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.core.list.ListMapper
import ru.anarkh.acomics.core.list.MultipleVHPagedListAdapter

class CatalogWidget(
	list: RecyclerView,
	quantityStringParser: FixedLocaleQuantityStringParser
) {

	private val comicsListElement = CatalogComicsListElement(quantityStringParser)
	private val sortItem = CatalogComicsSortListElement()
	private val adapterMapper = ListMapper(
		Triple(comicsListElement, CatalogViewHolder::class.java, CatalogComicsItem::class.java),
		Triple(sortItem, CatalogSortItemViewHolder::class.java, CatalogSortConfig::class.java)
	)
	private val adapter = MultipleVHPagedListAdapter(
		CatalogItemsDiffsValidator(),
		adapterMapper
	)

	init {
		list.layoutManager = LinearLayoutManager(list.context, RecyclerView.VERTICAL, false)
		list.adapter = adapter
	}

	fun updateList(pagedList: PagedList<Any>) {
		adapter.submitList(pagedList)
	}

	fun onComicsClick(listener: (link: String, pagesAmount: Int) -> Unit) {
		comicsListElement.onItemClickListener = listener
	}

	fun onSortIconClick(listener: (() -> Unit)) {
		sortItem.onSortIconClickListener = listener
	}
}