package ru.anarkh.acomics.comics

import android.app.Activity
import android.content.Intent
import ru.anarkh.acomics.core.DefaultActivity

class ComicsRouter(
	private val activity: DefaultActivity
) {

	fun finish(catalogId: String?) {
		val intent = Intent().putExtra(CATALOG_ID_EXTRA, catalogId)
		activity.setResult(Activity.RESULT_OK, intent)
		activity.finish()
	}
}