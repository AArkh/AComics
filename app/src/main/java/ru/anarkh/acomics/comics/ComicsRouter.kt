package ru.anarkh.acomics.comics

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import ru.anarkh.acomics.core.DefaultActivity
import ru.anarkh.acomics.core.error.ExceptionTelemetry

class ComicsRouter(
	private val activity: DefaultActivity,
	private val telemetry: ExceptionTelemetry
) {

	fun finish(catalogId: String?) {
		val intent = Intent().putExtra(CATALOG_ID_EXTRA, catalogId)
		activity.setResult(Activity.RESULT_OK, intent)
		activity.finish()
	}

	fun openBrowser(url: String) {
		try {
			val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
			activity.startActivity(intent)
		} catch (e: Exception) {
			Toast.makeText(activity, "Не получилось открыть ссылку =(", Toast.LENGTH_SHORT).show()
			telemetry.recordException(e)
		}
	}
}