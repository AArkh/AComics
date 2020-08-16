package ru.anarkh.acomics.main.info.widget

import android.widget.TextView

class InfoWidget(
	aboutApp: TextView,
	aboutAcomics: TextView,
	termsAndConditions: TextView,
	privacyPolicy: TextView
) {

	var onAboutAppClickListener: (() -> Unit)? = null
	var onAboutAcomicsClickListener: (() -> Unit)? = null
	var onTermsAndConditionsClickListener: (() -> Unit)? = null
	var onPrivacyPolicyClickListener: (() -> Unit)? = null

	init {
		aboutApp.setOnClickListener {
			onAboutAppClickListener?.invoke()
		}
		aboutAcomics.setOnClickListener {
			onAboutAcomicsClickListener?.invoke()
		}
		termsAndConditions.setOnClickListener {
			onTermsAndConditionsClickListener?.invoke()
		}
		privacyPolicy.setOnClickListener {
			onPrivacyPolicyClickListener?.invoke()
		}
	}
}