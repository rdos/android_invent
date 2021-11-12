package ru.smartro.inventory.base

import android.content.Context
import android.util.Log
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


class RestClient() : AbstractO() {
//    private val result = MutableLiveData<AbstractEntity>()
    private val mOkHttpClient: OkHttpClient = getHttpClient()

    private fun getHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = run {
            val httpLoggingInterceptor1 = HttpLoggingInterceptor { message -> Log.d("okhttp", message) }
            httpLoggingInterceptor1.apply {
                httpLoggingInterceptor1.level = HttpLoggingInterceptor.Level.BODY
            }
        }
        return OkHttpClient().newBuilder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(240, TimeUnit.SECONDS)
//            .addInterceptor(authInterceptor)
            .build()

    }

    fun newRequest(url: String) : Request.Builder {
        val token = db.loadConfig("Token")
        return Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${token}")
            .addHeader("Content-Type", "application/json; charset=utf-8")
    }


//найди 3 отличия_)))))))))))_))))

    fun newCall(request: Request, callback: Callback) {
        mOkHttpClient.newCall(request).enqueue(callback)
    }
}