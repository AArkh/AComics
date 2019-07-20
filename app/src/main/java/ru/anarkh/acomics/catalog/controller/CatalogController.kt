package ru.anarkh.acomics.catalog.controller

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ru.anarkh.acomics.catalog.CatalogRouter
import ru.anarkh.acomics.catalog.widget.CatalogWidget
import java.util.concurrent.Executors

class CatalogController(
	private val router: CatalogRouter,
	private val widget: CatalogWidget,
	lifecycleOwner: LifecycleOwner,
	dataSourceFactory : DataSource.Factory<Int, Any>
) {

	init {
		val pagedListConfig = PagedList.Config.Builder()
			.setEnablePlaceholders(false)
			.setPageSize(10)
			.build()
		val livePagedList = LivePagedListBuilder<Int, Any>(dataSourceFactory, pagedListConfig)
			.setInitialLoadKey(0)
			.setFetchExecutor(Executors.newSingleThreadExecutor())
			.build()

		livePagedList.observe(
			lifecycleOwner,
			Observer<PagedList<Any>> { widget.updateList(it) }
		)

		widget.onComicsClick { link: String, pagesAmount: Int -> router.openComicsPage(link, pagesAmount) }
	}
}