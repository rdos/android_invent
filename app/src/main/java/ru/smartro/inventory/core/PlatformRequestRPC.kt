package ru.smartro.inventory.core

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.PlatformEntityRealm
import java.io.IOException


class PlatformRequestRPC(val p_RestClient: RestClient): AbstractO(), Callback {
    private val result = MutableLiveData<List<PlatformEntityRealm>>()

    fun callAsyncRPC(rpcPlatformEntity: RPCPlatformEntity): MutableLiveData<List<PlatformEntityRealm>> {
        log.debug("callAsyncRPC.before" )
        val requestBody = rpcPlatformEntity.toRequestBody()
        val url = "https://worknote-back.stage.smartro.ru/api/rpc"
        val request = p_RestClient.newRequest(url).post(requestBody).build()
        p_RestClient.newCall(request, this)
        return result
    }

    override fun onFailure(call: Call, e: IOException) {
        throw IOException("Error response ${e}")
    }

    override fun onResponse(call: Call, response: Response) {
        log.debug("onResponse.before")

        val bodyString = response.body?.string()
        log.debug("onResponse. responseBody=${bodyString}")
        if (!response.isSuccessful) {
            throw IOException("Error response $response")
        }

//        val typeToken: Type = object : TypeToken<List<PlatformEntityRealm>>() {}.type
        val responseO = Gson().fromJson(bodyString, ResponseO::class.java)
//        val json = Gson().toJson(responseO.payload)
//        val platformEntityRealms = Gson().fromJson<List<PlatformEntityRealm>>(json, typeToken)
        log.info("onResponse responseO=${responseO}")
        db.save {
            for(payload in responseO.payload) {
                db.insert(payload)
            }
        }

        result.postValue(responseO.payload)
    }


//inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

}