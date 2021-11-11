package ru.smartro.inventory.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import ru.smartro.inventory.R
import java.io.IOException
import java.util.concurrent.TimeUnit

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import ru.smartro.inventory.LoginEntity
import ru.smartro.inventory.base.AbstractFragment


class OwnerFragment : AbstractFragment(), Callback, View.OnClickListener {

    companion object {
        fun newInstance() = OwnerFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
//        view.findViewById<AppCompatButton>(R.id.).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        getAsyncCall()
    }

    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url
            .newBuilder()
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer " + "")
            .url(newUrl)
            .build()
        chain.proceed(newRequest)
    }

    fun getAsyncCall() {
        val httpLoggingInterceptor = run {
            val httpLoggingInterceptor1 = HttpLoggingInterceptor { message -> Log.d("okhttp", message) }
            httpLoggingInterceptor1.apply {
                httpLoggingInterceptor1.level = HttpLoggingInterceptor.Level.BODY
            }
        }

        val httpClient = OkHttpClient().newBuilder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(240, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .build()

        val url = "https://auth.stage.smartro.ru/api/login"
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body =
            LoginEntity("admin@smartro.ru", "xot1ieG5ro~hoa,ng4Sh").toString().toRequestBody(JSON) // new
        log.warn( LoginEntity("admin@smartro.ru", "xot1ieG5ro~hoa,ng4Sh").toString())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        httpClient.newCall(request).enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        TODO("Not yet implemented")
    }

    override fun onResponse(call: Call, response: Response) {
        val responseBody = response.body
        if (!response.isSuccessful) {
            log.warn(response.headers.toString())
            throw IOException("Error response $response")
        }

        val gson = Gson()
//        val entity = gson.fromJson(responseBody?.string(), AuthResponse::class.java)
//        log.warn("entity=$entity")
    }




/*
object : Callback() {
            fun onFailure(call: Call?, e: IOException?) {
                Log.e(TAG, "error in getting response using async okhttp call")
            }

            @Throws(IOException::class)
            fun onResponse(call: Call?, response: Response) {
                val responseBody = response.body
                if (!response.isSuccessful) {
                    throw IOException("Error response $response")
                }
                Log.i(TAG, responseBody!!.string())
            }
        }
 */
}