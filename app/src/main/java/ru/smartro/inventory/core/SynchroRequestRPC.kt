package ru.smartro.inventory.core

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.URL_RPC_STAGE
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.PlatformEntityRealm
import java.io.IOException

class SynchroRequestRPC(): AbstractO(), Callback {
    private val result = MutableLiveData<Boolean>()

    fun callAsyncRPC(platformEntity: PlatformEntityRealm): MutableLiveData<Boolean> {
        log.debug("callAsyncRPC.before" )
        val synchroRequestEntity = SynchroRequestEntity()
        val ownerId = db.loadConfig("Owner")
        synchroRequestEntity.payload.organisation_id = ownerId.toInt()
        synchroRequestEntity.payload.data.add(platformEntity)
        val requestBody = synchroRequestEntity.toRequestBody()
        val restClient = RestClient()
        val request = restClient.newRequest(URL_RPC_STAGE).post(requestBody).build()
        restClient.newCall(request, this)
        return result
    }

    override fun onFailure(call: Call, e: IOException) {
//        throw IOException("Error response ${e}")
        result.postValue(false)

    }

    override fun onResponse(call: Call, response: Response) {
        log.debug("onResponse.before")

        val bodyString = response.body?.string()
        log.debug("onResponse. responseBody=${bodyString}")
        if (!response.isSuccessful) {
            result.postValue(false)
//            throw IOException("Error response $response")
        }
        result.postValue(true)
//        result.postValue(responseO.payload)
    }
}