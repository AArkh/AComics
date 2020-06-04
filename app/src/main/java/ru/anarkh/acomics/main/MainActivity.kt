package ru.anarkh.acomics.main

import android.os.Bundle
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.DefaultActivity

class MainActivity : DefaultActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val router = MainRouter(
			supportFragmentManager,
			R.id.container
		)
		NavBarController(
			router,
			NavBarWidget(findViewById(R.id.botton_navigation_view))
		)
	}
}
