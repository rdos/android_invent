package ru.smartro.inventory.core

import androidx.lifecycle.MutableLiveData
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.OwnerResponse
import java.io.IOException

class OwnerRequest(val p_RestClient: RestClient): AbstractO(), Callback {
    private val result = MutableLiveData<OwnerResponse>()
    fun callAsyncOwner(): MutableLiveData<OwnerResponse> {
        log.info("callAsyncOwner", "before")
        val url = "https://auth.stage.smartro.ru/api/owner"
        val request = p_RestClient.newRequest(url).get().build()
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
        val ownerResponse = OwnerResponse.from(response)
//        AppPreferences.accessToken = loginResponse.data.token
        db.saveOwnerList(ownerResponse)

//        val config = ConfigEntity(name="token", value=loginResponse.data.token)
//        p_RealmRepository.insertWayTask(config)
        result.postValue(ownerResponse)
    }


}