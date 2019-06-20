package ru.anarkh.acomics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import ru.anarkh.acomics.catalog.repository.CatalogHTMLParser
import ru.anarkh.acomics.catalog.repository.Repository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread {
            val client = OkHttpClient()
            Repository(client, CatalogHTMLParser()).getCatalog()
        }.start()
    }
}
