package ru.smartro.inventory.core

import androidx.lifecycle.MutableLiveData
import io.realm.DynamicRealm.Transaction.OnSuccess
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.Config
import ru.smartro.inventory.database.LoginResponse
import ru.smartro.inventory.getAUTHurl
import java.io.IOException

data class LoginNetworkResponse(
    var isLoading: Boolean = true,
    var isLoggedIn: Boolean = false,
    var error: Pair<Int, String>? = null
)

data class HttpErrorBody(
    var success: Boolean? = null,
    var message: String? = null
)

class LoginRequest(val p_RestClient: RestClient): AbstractO(), Callback {
    private val result = MutableLiveData(LoginNetworkResponse())

    fun callAsync(loginEntity: LoginEntity): MutableLiveData<LoginNetworkResponse> {
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
        result.postValue(LoginNetworkResponse(
            isLoading = false,
            error = Pair(-1, "Произошла ошибка сети, проверьте соединение с интернетом")
        ))
    }



    override fun onResponse(call: Call, response: Response) {
        val bodyString = response.body?.string()
        log.debug("onResponse.responseBody=${bodyString.toString()}")
        if (!response.isSuccessful || bodyString == null) {
            log.warn("onResponse. response.isSuccessful = ${response.isSuccessful}")
            result.postValue(LoginNetworkResponse(
                isLoading = false,
                error = Pair(response.code, bodyString ?: "Ошибка запроса: ${response.code}")
            ))
        } else {
            val loginResponse = LoginResponse.from(bodyString)
            val config = Config(name="Token", value=loginResponse.data.token)
            db.saveConfig(config)
            result.postValue(LoginNetworkResponse(isLoading = false, isLoggedIn = true))
//        AppDatabase.get().start(Setting("token", loginResponse.data.token))
        }
    }
}