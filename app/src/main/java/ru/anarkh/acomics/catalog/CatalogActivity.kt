package ru.anarkh.acomics.catalog

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.paging.DataSource
import okhttp3.OkHttpClient
import ru.anarkh.acomics.R
import ru.anarkh.acomics.catalog.controller.CatalogController
import ru.anarkh.acomics.catalog.model.CatalogComicsItem
import ru.anarkh.acomics.catalog.repository.CatalogCache
import ru.anarkh.acomics.catalog.repository.CatalogDataSource
import ru.anarkh.acomics.catalog.repository.CatalogHTMLParser
import ru.anarkh.acomics.catalog.repository.CatalogRepositoryImpl
import ru.anarkh.acomics.catalog.util.FixedLocaleQuantityStringParser
import ru.anarkh.acomics.catalog.widget.CatalogWidget

class CatalogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        toolbar.findViewById<ImageView>(R.id.toolbar_logo).setImageResource(R.drawable.logo)
        setSupportActionBar(toolbar)

        val localCache = ViewModelProviders
            .of(this)
            .get(CatalogCache::class.java)
        val repo = CatalogRepositoryImpl(OkHttpClient(), CatalogHTMLParser(), localCache)

        val parser = FixedLocaleQuantityStringParser(this)
        val widget = CatalogWidget(findViewById(R.id.list), parser)
        //todo Придумать механизм сохранения состояния через бандл и хранить позицию списка
        // Позицию прокинуть в loadInitial дата сурца
        CatalogController(
            CatalogRouter(this),
            widget,
            this,
            object : DataSource.Factory<Int, CatalogComicsItem>() {
                override fun create(): DataSource<Int, CatalogComicsItem> = CatalogDataSource(repo)
            }
        )
    }
}
