package ru.anarkh.acomics

import android.app.Application
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
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
		val imagePipelineConfig = OkHttpImagePipelineConfigFactory.newBuilder(
			this,
			Providers.getOkHttpClient()
		).build()
		BigImageViewer.initialize(FrescoImageLoader.with(this, imagePipelineConfig))
		ErrorStatProvider(
			this,
			BuildConfig.APPLICATION_ID,
			StackTraceHelper(),
			FirebaseCrashlytics.getInstance()
		)
	}
}