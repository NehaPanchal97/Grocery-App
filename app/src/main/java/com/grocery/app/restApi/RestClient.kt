package com.grocery.app.restApi

import android.content.Context
import com.grocery.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RestClient() {

    private val service: RestApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClient())
            .build()
        service = retrofit.create(RestApiService::class.java)
    }

    companion object {
        private lateinit var sInstance: RestClient
        private val lock = Any()

        fun getInstance(): RestClient {
            if (!::sInstance.isInitialized) {
                synchronized(lock) {
                    if (!::sInstance.isInitialized) {
                        sInstance = RestClient()
                    }
                }
            }
            return sInstance
        }
    }

    fun service(): RestApiService {
        return service
    }

    private fun createClient(): OkHttpClient {
        val okHttp = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttp.addInterceptor(loggingInterceptor)
        }
        return okHttp.build()
    }
}