package ru.anarkh.acomics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.paging.DataSource
import okhttp3.OkHttpClient
import ru.anarkh.acomics.catalog.controller.CatalogController
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.repository.CatalogCache
import ru.anarkh.acomics.catalog.repository.CatalogDataSource
import ru.anarkh.acomics.catalog.repository.CatalogHTMLParser
import ru.anarkh.acomics.catalog.repository.Repository
import ru.anarkh.acomics.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.catalog.widget.CatalogWidget

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val localCache = ViewModelProviders
            .of(this)
            .get(CatalogCache::class.java)
        val repo = Repository(OkHttpClient(), CatalogHTMLParser(), localCache)

        val parser = FixedLocaleQuantityStringParser(this)
        val widget = CatalogWidget(findViewById(R.id.list), parser)
        val controller = CatalogController(
            widget,
            this,
            object : DataSource.Factory<Int, CatalogComicsItem>() {
                override fun create(): DataSource<Int, CatalogComicsItem> = CatalogDataSource(repo)
            }
        )
    }
}
