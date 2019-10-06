package ru.anarkh.acomics.catalog.controller

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ru.anarkh.acomics.catalog.CatalogRouter
import ru.anarkh.acomics.catalog.model.CatalogSortingBy
import ru.anarkh.acomics.catalog.repository.CatalogRepository
import ru.anarkh.acomics.catalog.repository.CatalogSortConfigRepository
import ru.anarkh.acomics.catalog.widget.CatalogWidget
import ru.anarkh.acomics.catalog.widget.SortDialogWidget
import java.util.concurrent.Executors

class CatalogController(
	private val router: CatalogRouter,
	private val widget: CatalogWidget,
	private val sortDialogWidget: SortDialogWidget,
	lifecycleOwner: LifecycleOwner,
	dataSourceFactory : DataSource.Factory<Int, Any>,
	private val catalogRepository: CatalogRepository,
	private val sortConfigRepository: CatalogSortConfigRepository
) {

	init {
		val pagedListConfig = PagedList.Config.Builder()
			.setEnablePlaceholders(false)
			.setPageSize(10)
			.build()
		val livePagedList = LivePagedListBuilder(dataSourceFactory, pagedListConfig)
			.setInitialLoadKey(0)
			.setFetchExecutor(Executors.newSingleThreadExecutor())
			.build()

		livePagedList.observe(
			lifecycleOwner,
			Observer<PagedList<Any>> { widget.updateList(it) }
		)

		widget.onComicsClick { link: String, pagesAmount: Int ->
			router.openComicsPage(link, pagesAmount)
		}
		widget.onSortIconClick {
			sortDialogWidget.show()
		}

		sortDialogWidget.currentlyPickedSortingProvider = {
			sortConfigRepository.getActualSortingConfig().sorting
		}
		sortDialogWidget.onSortingItemClick = { pickedSort: CatalogSortingBy ->
			val currentCatalogConfig = sortConfigRepository.getActualSortingConfig()
			currentCatalogConfig.sorting = pickedSort
			sortConfigRepository.updateSortingConfig(currentCatalogConfig)
			catalogRepository.invalidateCache()
			livePagedList.value?.dataSource?.invalidate()
		}
		sortDialogWidget.riseFromTheDead()
	}
}