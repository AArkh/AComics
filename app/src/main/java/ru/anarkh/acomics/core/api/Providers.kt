package ru.anarkh.acomics.core.api

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address
import java.net.InetAddress

object Providers {

	val retrofit: Retrofit = Retrofit.Builder()
		.baseUrl("http://emptydomain.ru")
		.client(getOkHttpClient())
		.addConverterFactory(GsonConverterFactory.create(getGson()))
		.build()

	private fun getOkHttpClient(): OkHttpClient {
		return OkHttpClient.Builder()
			.dns { hostname: String -> //todo Убрать, когда починю эмуль
				if (hostname.contains("emptydomain.ru")) {
					val address: InetAddress = Inet4Address.getByName("194.87.146.71")
					mutableListOf(address)
				} else Dns.SYSTEM.lookup(hostname)
			}
			.addInterceptor {
				Log.d("12345", "requesting ${it.request().url()}")
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