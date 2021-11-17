package ru.smartro.inventory.core

import androidx.lifecycle.MutableLiveData
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.AUTH_STAGE
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.ConfigEntityRealm
import ru.smartro.inventory.database.LoginResponse
import java.io.IOException

class LoginRequest(val p_RestClient: RestClient): AbstractO(), Callback {
    private val result = MutableLiveData<ConfigEntityRealm>()

    fun callAsync(loginEntity: LoginEntity): MutableLiveData<ConfigEntityRealm> {
        log.info("callAsyncLogin", "before")

        val url = "$AUTH_STAGE/login"
        val request = p_RestClient.newRequest(url).post(loginEntity.toRequestBody()).build()
        log.info("callAsyncLogin")
        p_RestClient.newCall(request, this)
        return result
    }

    override fun onFailure(call: Call, e: IOException) {
        TODO("Not yet implemented")
    }



    override fun onResponse(call: Call, response: Response) {
        log.info("Aaa")
        val responseBody = response.body
        if (!response.isSuccessful) {
            log.warn(response.headers.toString())
            throw IOException("Error response $response")
        }
        val loginResponse = LoginResponse.from(response)
        log.info("onResponse.entity=$loginResponse")
//        AppPreferences.accessToken = loginResponse.data.token


        val config = ConfigEntityRealm(name="Token", value=loginResponse.data.token)
        db.saveConfig(config)
        result.postValue(config)
//        val handler = Handler(Looper.getMainLooper())
//        handler.post{
//            log.info("AAa")
//            result.value = config
//        }
//        AppDatabase.get().start(Setting("token", loginResponse.data.token))


    }
}