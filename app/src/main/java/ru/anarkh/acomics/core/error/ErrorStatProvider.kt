package ru.anarkh.acomics.core.error

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Debug
import android.os.Environment
import android.os.PowerManager
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.EnvironmentCompat
import com.google.android.gms.common.util.DeviceProperties.isTablet
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.anarkh.acomics.BuildConfig
import java.io.File

class ErrorStatProvider(
	private val context: Context,
	private val applicationId: String,
	private val stackTraceParser: StackTraceHelper,
	private val firebaseCrashlytics: FirebaseCrashlytics
) {

	init {
		val stats: Map<String, Any?> = provide()
		stats.forEach { entry: Map.Entry<String, Any?> ->
			firebaseCrashlytics.setCustomKey(entry.key, entry.value?.toString() ?: "null")
		}
	}

	private fun provide(): Map<String, Any?> {
		val stats = LinkedHashMap<String, Any?>()
		try {
			fillGeneralData(stats)
			fillConfigStat(stats)
			fillDisplayStat(stats)
			fillPowerStat(stats)
			fillInput(stats)
			fillStorageStat(stats)
			fillNetworkStat(stats)
		} catch (e: Exception) {
			stats["constant-stat-exception"] = stackTraceParser.getThrowableExtras(e)
		}
		return stats
	}

	private fun fillGeneralData(stats: LinkedHashMap<String, Any?>) {
		stats["app.is-debug"] = BuildConfig.DEBUG
		stats["app.installer"] = context.packageManager.getInstallerPackageName(applicationId);
		stats["device.model"] = getDeviceModel()
		stats["device.product"] = Build.PRODUCT
		stats["device.brand"] = Build.BRAND
		stats["device.sdk"] = Build.VERSION.SDK_INT
		stats["device.locale"] = context.resources.configuration.locale.language
		stats["device.is-tablet"] = isTablet(context.resources)
		stats["device.is-market-installed"] = isPlayStoreInstalled()
		stats["os.arch"] = System.getProperty("os.arch") ?: "null"
		stats["os.abis"] = getAbis()

		val manager = ContextCompat.getSystemService(context, ActivityManager::class.java)
		manager?.let {
			val memoryInfo = ActivityManager.MemoryInfo()
			manager.getMemoryInfo(memoryInfo)
			stats["os.memory.class"] = manager.memoryClass
			stats["os.memory.class-large"] = manager.largeMemoryClass
			stats["os.memory.is-low"] = memoryInfo.lowMemory
			stats["os.memory.is-low-ram-device"] = manager.isLowRamDevice
		}
	}

	/**
	 * Формируем стату конфигурации устройства.
	 */
	private fun fillConfigStat(stats: MutableMap<String, Any?>) {
		val configuration = context.resources.configuration
		stats["device.config.orientation"] = getOrientationText(configuration.orientation)
		stats["device.config.font-scale"] = configuration.fontScale
		stats["device.config.height-dp"] = configuration.screenHeightDp
		stats["device.config.width-dp"] = configuration.screenWidthDp
		stats["device.config.smallest-width-dp"] = configuration.smallestScreenWidthDp

		val uiModeManager = ContextCompat.getSystemService(context, UiModeManager::class.java)
		if (uiModeManager != null) {
			val uiMode =
				getUiMode(runSafelyOrNull { uiModeManager.currentModeType } ?: Int.MIN_VALUE)
			val nightMode =
				getNightMode(runSafelyOrNull { uiModeManager.nightMode } ?: Int.MIN_VALUE)
			stats["device.config.ui-mode"] = uiMode
			stats["device.config.night-mode"] = nightMode
		}
	}

	/**
	 * Формируем стату по метрикам дисплея.
	 */
	private fun fillDisplayStat(stats: MutableMap<String, Any?>) {
		val displayMetrics = context.resources.displayMetrics
		stats["device.display.density"] = displayMetrics.density
		stats["device.display.density-dpi"] = displayMetrics.densityDpi
		stats["device.display.density-scaled"] = displayMetrics.scaledDensity
		stats["device.display.width"] = displayMetrics.widthPixels
		stats["device.display.height"] = displayMetrics.heightPixels
	}

	/**
	 * Формируем стату режиму энергопотребления.
	 */
	private fun fillPowerStat(stats: MutableMap<String, Any?>) {
		val powerManager = ContextCompat.getSystemService(context, PowerManager::class.java)
		if (powerManager != null) {
			stats["device.power.interactive"] = runSafelyOrNull { powerManager.isInteractive }
			stats["device.power.power-save"] = runSafelyOrNull { powerManager.isPowerSaveMode }
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				stats["device.power.idle-mode"] = runSafelyOrNull { powerManager.isDeviceIdleMode }
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				val isModeSupported = runSafelyOrNull {
					powerManager.isSustainedPerformanceModeSupported
				}
				stats["device.power.supports-susperf"] = isModeSupported
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				val mode = runSafelyOrNull { powerManager.locationPowerSaveMode } ?: Int.MIN_VALUE
				stats["device.power.location-power-save"] = getLocationPowerSaveMode(mode)
			}
		}
		val activityManager =
			ContextCompat.getSystemService(context, ActivityManager::class.java) ?: return
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			val isBackgroundRestricted = runSafelyOrNull {
				activityManager.isBackgroundRestricted
			}
			stats["device.power.is-background-restricted"] = isBackgroundRestricted
		}
	}

	private fun fillInput(stats: MutableMap<String, Any?>) {
		val inputMethodManager =
			ContextCompat.getSystemService(context, InputMethodManager::class.java)
		if (inputMethodManager != null) {
			stats["device.input.active"] = inputMethodManager.isActive
			stats["device.input.fullscreen-mode"] = inputMethodManager.isFullscreenMode
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			stats["stats.gc"] = Debug.getRuntimeStats()
		}
	}

	private fun fillStorageStat(stats: MutableMap<String, Any?>) {
		val externalDirs = getExternalDirs()
		stats["device.storage.externals-count"] = externalDirs.size
		stats["device.storage.external-dirs"] = externalDirs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			stats["device.storage.volumes"] = getStorageVolumes()
		}
	}

	/**
	 * Формируем стату состоянию сети.
	 */
	private fun fillNetworkStat(stats: MutableMap<String, Any?>) {
		stats["device.airplane-mode"] = isAirplaneModeOn()

		val manager = ContextCompat.getSystemService(
			context, ConnectivityManager::class.java
		) ?: return
		stats["device.network.info"] = runSafelyOrNull { manager.activeNetworkInfo?.extraInfo }
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			val restrictBg = runSafelyOrNull { manager.restrictBackgroundStatus } ?: Int.MIN_VALUE
			stats["device.network.restrict-bg"] = getRestrictBackgroundStatus(restrictBg)
		}
	}

	private fun isAirplaneModeOn(): Boolean {
		return Settings.Global.getInt(
			context.contentResolver,
			Settings.Global.AIRPLANE_MODE_ON,
			0
		) != 0
	}

	/**
	 * @see Configuration.orientation
	 */
	private fun getOrientationText(orientation: Int): String = when (orientation) {
		Configuration.ORIENTATION_LANDSCAPE -> "LANDSCAPE"
		Configuration.ORIENTATION_PORTRAIT -> "PORTRAIT"
		else -> "UNKNOWN"
	}

	/**
	 * @see UiModeManager.getCurrentModeType
	 */
	private fun getUiMode(mode: Int): String = when (mode) {
		Configuration.UI_MODE_TYPE_NORMAL -> "NORMAL"
		Configuration.UI_MODE_TYPE_DESK -> "DESK"
		Configuration.UI_MODE_TYPE_CAR -> "CAR"
		Configuration.UI_MODE_TYPE_TELEVISION -> "TELEVISION"
		Configuration.UI_MODE_TYPE_APPLIANCE -> "APPLIANCE"
		Configuration.UI_MODE_TYPE_WATCH -> "WATCH"
		Configuration.UI_MODE_TYPE_VR_HEADSET -> "VR_HEADSET"
		else -> "UNDEFINED"
	}

	/**
	 * @see UiModeManager.getNightMode
	 */
	private fun getNightMode(mode: Int): String = when (mode) {
		UiModeManager.MODE_NIGHT_NO -> "NO"
		UiModeManager.MODE_NIGHT_YES -> "YES"
		UiModeManager.MODE_NIGHT_AUTO -> "AUTO"
		else -> "UNDEFINED"
	}

	/**
	 * @see PowerManager.getLocationPowerSaveMode
	 */
	private fun getLocationPowerSaveMode(mode: Int): String = when (mode) {
		PowerManager.LOCATION_MODE_NO_CHANGE -> "NO_CHANGE"
		PowerManager.LOCATION_MODE_GPS_DISABLED_WHEN_SCREEN_OFF -> "GPS_DISABLED_WHEN_SCREEN_OFF"
		PowerManager.LOCATION_MODE_ALL_DISABLED_WHEN_SCREEN_OFF -> "ALL_DISABLED_WHEN_SCREEN_OFF"
		PowerManager.LOCATION_MODE_FOREGROUND_ONLY -> "FOREGROUND_ONLY"
		else -> "UNKNOWN"
	}

	/**
	 * @see ConnectivityManager.getRestrictBackgroundStatus
	 */
	private fun getRestrictBackgroundStatus(status: Int): String = when (status) {
		ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED -> "DISABLED"
		ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED -> "WHITELISTED"
		ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED -> "ENABLED"
		else -> "UNKNOWN"
	}

	private fun getExternalDirs(): List<String> {
		val result = mutableListOf<String>()
		val dirs = ContextCompat.getExternalFilesDirs(context, null)
		dirs.forEach { dir: File? ->
			if (dir == null) {
				result.add("null (state = unknown)")
				return@forEach
			}

			val state = EnvironmentCompat.getStorageState(dir)
			val removable = runSafelyOrNull { Environment.isExternalStorageRemovable(dir) }
			val emulated = runSafelyOrNull { Environment.isExternalStorageEmulated(dir) }
			result.add("$dir (state = $state, removable = $removable, emulated = $emulated)")
		}
		return result
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private fun getStorageVolumes(): List<String> {
		val result = arrayListOf<String>()
		val manager = ContextCompat.getSystemService(
			context,
			StorageManager::class.java
		) ?: return result
		val storageVolumes = runSafelyOrNull { manager.storageVolumes } ?: return result
		storageVolumes.forEach { storageVolume: StorageVolume? ->
			if (storageVolume == null) {
				result.add("null")
				return@forEach
			}

			val volume = StringBuilder(storageVolume.getDescription(context))
				.append(" (")
				.append("emulated = ").append(storageVolume.isEmulated)
				.append(", ")
				.append("primary = ").append(storageVolume.isPrimary)
				.append(", ")
				.append("removable = ").append(storageVolume.isRemovable)
				.append(", ")
				.append("state = ").append(storageVolume.state)
				.append(", ")
				.append("uuid = ").append(storageVolume.uuid)
				.append(")")
			result.add(volume.toString())
		}
		return result
	}


	private fun getAbis(): String {
		val abis: Array<String> = Build.SUPPORTED_ABIS
		return abis.contentToString()
	}


	@SuppressLint("DefaultLocale")
	private fun getDeviceModel(): String? {
		val manufacturer = Build.MANUFACTURER.capitalize()
		val model = Build.MODEL
		return if (model.startsWith(manufacturer)) {
			model
		} else {
			"$manufacturer $model"
		}
	}

	private fun isPlayStoreInstalled(): Boolean {
		return isAppInstalled("com.android.vending") || isAppInstalled("com.google.market")
	}

	private fun isAppInstalled(packageId: String): Boolean {
		return try {
			context.packageManager.getPackageInfo(packageId, PackageManager.GET_ACTIVITIES)
			true
		} catch (e: Exception) {
			false
		}
	}

	private fun <R> runSafelyOrNull(block: () -> R?): R? {
		return try {
			block.invoke()
		} catch (e: Throwable) {
			null
		}
	}
}
