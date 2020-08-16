package ru.anarkh.acomics.main.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.anarkh.acomics.R
import ru.anarkh.acomics.core.DefaultFragment
import ru.anarkh.acomics.main.info.controller.InfoController
import ru.anarkh.acomics.main.info.widget.InfoWidget

class InfoFragment : DefaultFragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_info, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val widget = InfoWidget(
			view.findViewById(R.id.about_app),
			view.findViewById(R.id.about_acomics),
			view.findViewById(R.id.terms_and_conditions),
			view.findViewById(R.id.privacy_policy)
		)
		InfoController(
			widget,
			InfoRouter(this)
		)
	}
}