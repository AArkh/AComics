package ru.anarkh.acomics.catalog.widget

import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.anarkh.acomics.catalog.controller.CatalogItemsDiffsValidator
import ru.anarkh.acomics.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.core.LoadingElement
import ru.anarkh.acomics.core.MultipleVHPagedListAdapter

class CatalogWidget(
	list: RecyclerView,
	quantityStringParser: FixedLocaleQuantityStringParser
) {

	private val comicsListElement = CatalogComicsListElement(quantityStringParser)
	private val sortItem = CatalogComicsSortListElement()
	private val adapter = MultipleVHPagedListAdapter(
		CatalogItemsDiffsValidator(),
		comicsListElement,
		sortItem,
		LoadingElement()
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
}