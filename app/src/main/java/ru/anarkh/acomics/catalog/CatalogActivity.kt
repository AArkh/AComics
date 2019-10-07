package ru.anarkh.acomics.catalog

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.paging.DataSource
import ru.anarkh.acomics.R
import ru.anarkh.acomics.catalog.controller.CatalogController
import ru.anarkh.acomics.catalog.repository.*
import ru.anarkh.acomics.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.catalog.widget.CatalogWidget
import ru.anarkh.acomics.catalog.widget.SortDialogWidget
import ru.anarkh.acomics.core.DefaultActivity
import ru.anarkh.acomics.core.web.HttpClientProvider

class CatalogActivity : DefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        toolbar.findViewById<ImageView>(R.id.toolbar_logo).setImageResource(R.drawable.logo)
        setSupportActionBar(toolbar)

        val sortConfigRepository = CatalogSortConfigRepository(this)
        val localCache = ViewModelProviders
            .of(this)
            .get(CatalogCache::class.java)
        val repo = CatalogRepositoryImpl(
            HttpClientProvider.getHttpClient(),
            CatalogHTMLParser(),
            localCache,
            sortConfigRepository
        )

        val parser = FixedLocaleQuantityStringParser(this)
        val widget = CatalogWidget(findViewById(R.id.list), parser)
        CatalogController(
            CatalogRouter(this),
            widget,
            SortDialogWidget(this, lifecycle, stateRegistry),
            this,
            object : DataSource.Factory<Int, Any>() {
                override fun create(): DataSource<Int, Any> = CatalogDataSource(
                    repo, sortConfigRepository, stateRegistry
                )
            },
            repo,
            sortConfigRepository
        )
    }
}
