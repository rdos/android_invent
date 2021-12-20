package ru.smartro.inventory.core

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.Config
import ru.smartro.inventory.database.PlatformEntityRealm
import ru.smartro.inventory.getRpcUrl
import java.io.IOException


class PlatformRequestRPC(val p_RestClient: RestClient, val p_context: Context?): AbstractO(), Callback {
    private val result = MutableLiveData<List<PlatformEntityRealm>>()

    fun callAsyncRPC(rpcPlatformEntity: RPCPlatformEntity): MutableLiveData<List<PlatformEntityRealm>> {
        log.debug("callAsyncRPC.before" )

        val requestBody = rpcPlatformEntity.toRequestBody()
        val request = p_RestClient.newRequest(getRpcUrl()).post(requestBody).build()
        p_RestClient.newCall(request, this)
        return result
    }

    override fun onFailure(call: Call, e: IOException) {
//        Toast. throw IOException("Error response ${e}")
    }

    override fun onResponse(call: Call, response: Response) {
        log.debug("onResponse.before")

        val bodyString = response.body?.string()
        log.debug("onResponse. responseBody=${bodyString}")
        if (!response.isSuccessful) {
            throw IOException("Error response $response")
        }

//        val typeToken: Type = object : TypeToken<List<PlatformEntityRealm>>() {}.type
        var responseO = ResponseO()
        try {
            responseO = Gson().fromJson(bodyString, ResponseO::class.java)
            //        val json = Gson().toJson(responseO.payload)
//        val platformEntityRealms = Gson().fromJson<List<PlatformEntityRealm>>(json, typeToken)
            db.save {
                for(payload in responseO.payload) {
                    payload.convertFromServData()

                    log.debug("save_-PlatformRequestRPC.uuid=${payload.uuid}")
                    db.insert(payload)
                }
            }
            val configEntityRealm = Config("is_allowed_inventory_get_platforms", false.toString())
            db.saveConfig(configEntityRealm)
            result.postValue(responseO.payload)
        } catch (e: Exception) {
//            p_context?.let {
//                Toast.makeText(p_context, bodyString, Toast.LENGTH_SHORT).show()
//            }

        }

    }


//inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

}