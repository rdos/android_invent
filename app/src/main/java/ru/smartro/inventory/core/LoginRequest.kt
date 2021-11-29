package ru.smartro.inventory.core

import androidx.lifecycle.MutableLiveData
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.ConfigEntityRealm
import ru.smartro.inventory.database.LoginResponse
import ru.smartro.inventory.getAUTHurl
import java.io.IOException

class LoginRequest(val p_RestClient: RestClient): AbstractO(), Callback {
    private val result = MutableLiveData<Boolean>()

    fun callAsync(loginEntity: LoginEntity): MutableLiveData<Boolean> {
        log.info("callAsyncLogin", "before")

        val url = getAUTHurl("login")
        val request = p_RestClient.newRequest(url).post(loginEntity.toRequestBody()).build()
        log.info("callAsyncLogin")
        p_RestClient.newCall(request, this)
        return result
    }

    override fun onFailure(call: Call, e: IOException) {
//        TODO("Not yet implemented")
        log.error("onFailure", e)
        result.postValue(false)
    }



    override fun onResponse(call: Call, response: Response) {
        val bodyString = response.body?.string()
        log.debug("onResponse.responseBody=${bodyString.toString()}")
        if (!response.isSuccessful) {
            log.warn("onResponse. response.isSuccessful = ${response.isSuccessful}")
            result.postValue(false)
            return
        }
        if (bodyString == null ) {
            result.postValue(false)
            return
        } else {
            val loginResponse = LoginResponse.from(bodyString)
            val config = ConfigEntityRealm(name="Token", value=loginResponse.data.token)
            db.saveConfig(config)
            result.postValue(true)
//        AppDatabase.get().start(Setting("token", loginResponse.data.token))
        }
    }
}