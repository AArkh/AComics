package ru.anarkh.acomics.core

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.anarkh.acomics.BuildConfig
import ru.anarkh.acomics.core.db.AppDatabase
import ru.anarkh.acomics.core.db.migration.MigrationV1V2
import ru.anarkh.acomics.main.favorite.model.FavoritesRepository
import java.net.Inet4Address

object Providers {

	@JvmStatic
	fun init(context: Context) {
		val appContext = context.applicationContext
		appDatabase = Room.databaseBuilder(appContext, AppDatabase::class.java, "app_database.db")
			.addMigrations(MigrationV1V2())
			.build()
	}

	val retrofit: Retrofit by lazy {
		Retrofit.Builder()
			.baseUrl("http://emptydomain.ru")
			.client(getOkHttpClient())
			.addConverterFactory(GsonConverterFactory.create(getGson()))
			.build()
	}

	val favoriteRepository: FavoritesRepository by lazy {
		FavoritesRepository(appDatabase.favoritesDao())
	}

	private lateinit var appDatabase: AppDatabase

	fun getOkHttpClient(): OkHttpClient {
		return OkHttpClient.Builder()
			.dns { hostname: String ->
				if (!BuildConfig.DEBUG) {
					return@dns Dns.SYSTEM.lookup(hostname)
				}
				return@dns when {
					hostname.contains("emptydomain.ru") ->
						mutableListOf(Inet4Address.getByName("10.0.2.2")) // remote 194.87.146.71
					hostname.contains("acomics.ru") ->
						mutableListOf(Inet4Address.getByName("88.198.58.142"))
					else -> Dns.SYSTEM.lookup(hostname)
				}
			}
			.addInterceptor {
				if (BuildConfig.DEBUG) {
					Log.d("12345", "requesting ${it.request().url()}")
				}
				return@addInterceptor it.proceed(it.request())
			}
			.build()
	}

	private fun getGson(): Gson {
		return GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create()
	}
}