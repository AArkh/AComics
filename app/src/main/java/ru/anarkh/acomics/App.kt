package ru.anarkh.acomics

import android.app.Application
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import ru.anarkh.acomics.core.api.Providers

class App : Application() {

	override fun onCreate() {
		super.onCreate()
		Providers.init(this)
		BigImageViewer.initialize(FrescoImageLoader.with(this))
	}
}