package ru.anarkh.acomics

import android.app.Application
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader

class App : Application() {

	override fun onCreate() {
		super.onCreate()
		BigImageViewer.initialize(FrescoImageLoader.with(this))
	}
}