package ru.anarkh.acomics.main.info

import androidx.fragment.app.Fragment
import ru.anarkh.acomics.main.info.web.LocalHtmlWebViewActivity

class InfoRouter(
	private val fragment: Fragment
) {

	fun openWebView(url: String) {
		val intent = LocalHtmlWebViewActivity.newIntent(fragment.requireContext(), url)
		fragment.startActivity(intent)
	}
}