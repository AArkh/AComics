package ru.anarkh.acomics.main.info.web

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.anarkh.acomics.R

private const val EXTRA_URL = "url"

class LocalHtmlWebViewActivity : AppCompatActivity() {

	companion object {
		fun newIntent(context: Context, url: String): Intent {
			return Intent(context, LocalHtmlWebViewActivity::class.java).putExtra(EXTRA_URL, url)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_local_html_web_view)

		val url: String? = intent.getStringExtra(EXTRA_URL)
		if (url == null) {
			Toast.makeText(
				this,
				"Что-то пошло не так =_=",
				Toast.LENGTH_SHORT
			).show()
			finish()
			return
		}

		val webView: WebView = findViewById(R.id.web_view)
		webView.settings.defaultTextEncodingName = "utf-8"
		webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
		webView.settings.defaultFontSize = 16
		webView.loadUrl(url)
	}
}