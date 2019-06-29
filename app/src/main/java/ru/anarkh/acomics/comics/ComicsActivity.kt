package ru.anarkh.acomics.comics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import ru.anarkh.acomics.R
import ru.anarkh.acomics.comics.repository.ComicsHTMLParser
import ru.anarkh.acomics.comics.repository.ComicsRepositoryImpl
import ru.anarkh.acomics.comics.widget.ComicsWidget

class ComicsActivity : AppCompatActivity() {

	companion object {
		private const val COMICS_LINK_EXTRA = "comics_link_extra"

		fun intent(context: Context, comicsLink: String) : Intent {
			return Intent(context, ComicsActivity::class.java)
				.putExtra(COMICS_LINK_EXTRA, comicsLink)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_comics)

		val comicsLink = intent.getStringExtra(COMICS_LINK_EXTRA)
		if (TextUtils.isEmpty(comicsLink)) {
			finish()
			return
		}

		val widget = ComicsWidget(findViewById(R.id.zoomable_image))

		val repo = ComicsRepositoryImpl(comicsLink, OkHttpClient(), ComicsHTMLParser())
		Thread {
			val model = repo.getComicsPage(1)
			Handler(Looper.getMainLooper()).post{
				widget.setImage(model)
			}
		}.start()
	}
}
