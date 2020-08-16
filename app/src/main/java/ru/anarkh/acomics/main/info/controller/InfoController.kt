package ru.anarkh.acomics.main.info.controller

import ru.anarkh.acomics.main.info.InfoRouter
import ru.anarkh.acomics.main.info.widget.InfoWidget

private const val ASSET_URI = "file:///android_asset/"
private const val ABOUT_APP_HTML = "about_app.html"
private const val ACOMICS_RULES_HTML = "acomics_rules.html"
private const val TERMS_AND_CONDITIONS_HTML = "terms_and_conditions.html"
private const val PRIVACY_POLICY_HTML = "privacy_policy.html"

class InfoController(
	widget: InfoWidget,
	private val router: InfoRouter
) {

	init {
		widget.onAboutAppClickListener = {
			router.openWebView("$ASSET_URI$ABOUT_APP_HTML")
		}
		widget.onAboutAcomicsClickListener = {
			router.openWebView("$ASSET_URI$ACOMICS_RULES_HTML")
		}
		widget.onTermsAndConditionsClickListener = {
			router.openWebView("$ASSET_URI$TERMS_AND_CONDITIONS_HTML")
		}
		widget.onPrivacyPolicyClickListener = {
			router.openWebView("$ASSET_URI$PRIVACY_POLICY_HTML")
		}
	}
}