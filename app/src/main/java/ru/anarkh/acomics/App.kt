package ru.anarkh.acomics

import android.app.Application
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.anarkh.acomics.core.Providers
import ru.anarkh.acomics.core.error.ErrorStatProvider
import ru.anarkh.acomics.core.error.StackTraceHelper

class App : Application() {

	override fun onCreate() {
		super.onCreate()
		Providers.init(this)
		BigImageViewer.initialize(FrescoImageLoader.with(this))
		ErrorStatProvider(
			this,
			BuildConfig.APPLICATION_ID,
			StackTraceHelper(),
			FirebaseCrashlytics.getInstance()
		)
	}
}